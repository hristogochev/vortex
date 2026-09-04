package io.github.hristogochev.vortex.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import io.github.hristogochev.vortex.model.ScreenModelStore
import io.github.hristogochev.vortex.navigator.LocalNavigatorStateHolder
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.stack.StackEvent
import io.github.hristogochev.vortex.util.currentOrThrow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.cancellation.CancellationException

/**
 *  Displays the current screen of a [Navigator] with an predictive-back transition.
 *
 *  The predictive-back transition must be of type [ScreenTransitionPredictiveBack].
 *
 *  Takes in a default [ScreenTransition] for when a screen enters and leaves the visible area.
 *
 *  Each [Screen] can have it's own transition for when it enters and leaves the visible area.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun CurrentScreenPredictiveBack(
    navigator: Navigator,
    defaultPredictiveBackTransition: ScreenTransitionPredictiveBack,
    enabled: Boolean = true,
    swipeSides: List<Int> = listOf(0, 1),
    defaultOnScreenAppearTransition: ScreenTransition? = null,
    defaultOnScreenDisappearTransition: ScreenTransition? = null,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    contentKey: (Screen) -> Any = { it.key },
    content: @Composable AnimatedVisibilityScope.(Screen) -> Unit = { it.Content() },
) {
    // This updates instantly when the stack changes
    var unexpectedScreenStateKeysQueue by rememberSaveable(saver = unexpectedScreenStateKeysQueueSaver()) {
        mutableStateOf(emptySet())
    }

    val oldScreens = navigator.items

    DisposableEffect(oldScreens) {
        onDispose {
            val oldScreenStateKeys = oldScreens.map { "${it.key}:${navigator.key}" }
            val currentScreenStateKeys = navigator.items.map { "${it.key}:${navigator.key}" }
            val unexpectedScreenStateKeys = oldScreenStateKeys.filter {
                it !in currentScreenStateKeys
            }
            unexpectedScreenStateKeysQueue += unexpectedScreenStateKeys
        }
    }

    // Make sure the transition state's target state is always the state of the latest screen
    val transitionState = remember {
        SeekableTransitionState(navigator.current)
    }

    val transition = rememberTransition(transitionState, label = "entry")

    var currentTransitionShouldBePredictiveBack by remember { mutableStateOf(false) }

    var isAnimatingNavigation by remember { mutableStateOf(false) }

    LaunchedEffect(navigator.current) {
        // During an automatic event (push/pop/replace),
        // transitionState.targetState lags behind navigator.current,
        // that signals that the current transition shouldn't be a predictive back transition.
        // During a manual predictive back transition transitionState.target doesn't lag behind,
        // That means that currentTransitionShouldBePredictiveBack remains set to true and only gets toggled,
        // When a manual transition takes place.
        if (transitionState.targetState != navigator.current) {
            currentTransitionShouldBePredictiveBack = false
        }
        isAnimatingNavigation = true
        transitionState.animateTo(navigator.current)
        isAnimatingNavigation = false
    }

    val prevScreen by remember(navigator.current) {
        derivedStateOf {
            if (navigator.items.size < 2) {
                return@derivedStateOf null
            }
            navigator.items[navigator.items.lastIndex - 1]
        }
    }

    val currentScreenCanPop by remember(navigator.current) {
        derivedStateOf {
            navigator.current.canPop
        }
    }

    val coroutineScope = rememberCoroutineScope()

    PredictiveBackHandler(
        enabled = enabled && prevScreen != null && currentScreenCanPop,
        onBack = onBack@{ progress ->
            val prevScreen = prevScreen ?: return@onBack

            // Whether this gesture ever drove a transition
            var seeking = false

            progress
                .filter { backEvent ->
                    swipeSides.contains(backEvent.swipeEdge)
                }.onEach { backEvent ->
                    // Do not seek and follow finger if we are animating automatically with a push/pop/replace
                    if (!seeking && isAnimatingNavigation) return@onEach
                    seeking = true
                    currentTransitionShouldBePredictiveBack = true
                    transitionState.seekTo(backEvent.progress, prevScreen)
                }.onCompletion { cause ->
                    if (!seeking) return@onCompletion
                    when (cause) {
                        null -> {
                            navigator.pop()
                        }

                        is CancellationException -> {
                            coroutineScope.launch {
                                val mutex = Mutex()
                                animate(
                                    transitionState.fraction,
                                    0f,
                                    animationSpec = defaultPredictiveBackTransition.cancelAnimationSpec
                                ) { value, _ ->
                                    launch {
                                        mutex.withLock {
                                            transitionState.seekTo(value)
                                            if (value == 0f) {
                                                transitionState.snapTo(navigator.current)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> Unit
                    }
                }.collect()
        })

    val stateHolder = LocalNavigatorStateHolder.currentOrThrow

    // This updates when the transition is done
    if (transition.currentState == transition.targetState) {
        LaunchedEffect(transition.currentState) {
            // We perform a check again, we remove all from the unexpected queue that are actually expected
            val currentScreenStateKeys = navigator.items.map { "${it.key}:${navigator.key}" }

            val unexpectedScreenStateKeys = unexpectedScreenStateKeysQueue
                .filter { it !in currentScreenStateKeys }

            if (unexpectedScreenStateKeys.isNotEmpty()) {

                for (unexpectedScreenStateKey in unexpectedScreenStateKeys) {
                    ScreenModelStore.dispose(unexpectedScreenStateKey)

                    ScreenDisposableEffectStore.dispose(unexpectedScreenStateKey)

                    stateHolder.removeState(unexpectedScreenStateKey)

                    navigator.disassociateScreenStateKey(unexpectedScreenStateKey)
                }

                navigator.clearEvent()
            }

            unexpectedScreenStateKeysQueue = emptySet()
        }
    }

    transition.AnimatedContent(
        transitionSpec = {
            val transition = when {
                currentTransitionShouldBePredictiveBack -> initialState.predictiveBackTransition
                    ?: defaultPredictiveBackTransition

                navigator.lastEvent == StackEvent.Pop -> initialState.onDisappearTransition
                    ?: defaultOnScreenDisappearTransition

                else -> targetState.onAppearTransition ?: defaultOnScreenAppearTransition
            }

            // AnimatedContent freezes a screen's zIndex at the value it entered with, so the depth
            // is used to order them, with any declared zIndex applied on top of it as an offset
            val targetDepth = navigator.items.indexOf(targetState).coerceAtLeast(0).toFloat()

            ContentTransform(
                targetContentEnter = transition?.enter() ?: EnterTransition.None,
                initialContentExit = transition?.exit() ?: ExitTransition.None,
                targetContentZIndex = targetDepth + (transition?.zIndex ?: 0f),
                sizeTransform = transition?.sizeTransform() ?: SizeTransform()
            )
        },
        contentAlignment = contentAlignment,
        contentKey = contentKey,
        modifier = modifier
    ) { screen ->
        screen.render {
            content(it)
        }
    }
}

/**
 * Just an utility saver for the screens that should be disposed during a transition.
 */
private fun unexpectedScreenStateKeysQueueSaver(): Saver<MutableState<Set<String>>, List<String>> {
    return Saver(
        save = { it.value.toList() },
        restore = { mutableStateOf(it.toSet()) }
    )
}