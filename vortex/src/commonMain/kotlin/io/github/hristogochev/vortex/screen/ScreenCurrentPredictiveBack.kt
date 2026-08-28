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
import androidx.compose.runtime.rememberUpdatedState
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

    LaunchedEffect(navigator.current) {
        transitionState.animateTo(navigator.current)
    }

    // Un-keyed derived state: it reads snapshot state (navigator.items), so it
    // stays current without being recreated per navigation - the remembered
    // onBack below can safely capture the State instance itself.
    val prevScreenState = remember {
        derivedStateOf {
            if (navigator.items.size < 2) {
                return@derivedStateOf null
            }
            navigator.items[navigator.items.lastIndex - 1]
        }
    }
    val prevScreen by prevScreenState

    val currentScreenCanPop by remember {
        derivedStateOf {
            navigator.current.canPop
        }
    }

    var isInPredictiveBack by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val updatedPredictiveBackTransition = rememberUpdatedState(defaultPredictiveBackTransition)
    val updatedSwipeSides = rememberUpdatedState(swipeSides)

    // The onBack lambda must be ONE stable instance for the lifetime of this
    // composition: Compose's PredictiveBackHandler keys the platform handler on
    // the lambda instance, so a fresh instance created by a mid-gesture
    // recomposition (the first seekTo triggers one) tears the handler down and
    // cancels the in-flight gesture - on iOS the screen tracked the finger for a
    // few dp and then snapped back. Live values are read through State instances
    // captured by the lambda instead of raw captures. The lambda's type is never
    // written out because BackEventCompat is not part of the stable public API on
    // all targets - rememberOnBack lets it flow in by inference.
    PredictiveBackHandler(
        enabled = enabled && prevScreen != null && currentScreenCanPop,
        onBack = rememberOnBack(
            navigator, transitionState, coroutineScope,
            onBack = onBack@{ progress ->
                val prevScreen = prevScreenState.value ?: return@onBack

                progress
                    .filter { backEvent ->
                        updatedSwipeSides.value.contains(backEvent.swipeEdge)
                    }.onEach { backEvent ->
                        if (!isInPredictiveBack && transitionState.fraction > 0) return@onEach
                        isInPredictiveBack = true
                        transitionState.seekTo(backEvent.progress, prevScreen)
                    }.onCompletion { cause ->
                        if (!isInPredictiveBack) return@onCompletion
                        when (cause) {
                            null -> {
                                navigator.pop()
                                isInPredictiveBack = false
                            }

                            is CancellationException -> {
                                coroutineScope.launch {
                                    val mutex = Mutex()
                                    animate(
                                        transitionState.fraction,
                                        0f,
                                        animationSpec = updatedPredictiveBackTransition.value.cancelAnimationSpec
                                    ) { value, _ ->
                                        launch {
                                            mutex.withLock {
                                                transitionState.seekTo(value)
                                                if (value == 0f) {
                                                    isInPredictiveBack = false
                                                    transitionState.snapTo(navigator.current)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else -> {
                                isInPredictiveBack = false
                            }
                        }
                    }.collect()
            })
    )

    var currentContentTransform by remember { mutableStateOf<ContentTransform?>(null) }
    transition.AnimatedContent(
        transitionSpec = {
            val transition = when {
                isInPredictiveBack -> initialState.predictiveBackTransition
                    ?: defaultPredictiveBackTransition

                navigator.lastEvent == StackEvent.Pop -> initialState.onDisappearTransition
                    ?: defaultOnScreenDisappearTransition

                else -> targetState.onAppearTransition ?: defaultOnScreenAppearTransition
            }

            ContentTransform(
                targetContentEnter = transition?.enter() ?: EnterTransition.None,
                initialContentExit = transition?.exit() ?: ExitTransition.None,
                targetContentZIndex = transition?.zIndex ?: 0f,
                sizeTransform = transition?.sizeTransform() ?: SizeTransform()
            ).also {
                currentContentTransform = it
            }
        },
        contentAlignment = contentAlignment,
        contentKey = contentKey,
        modifier = modifier
    ) { screen ->
        if (this.transition.targetState == this.transition.currentState) {
            val stateHolder = LocalNavigatorStateHolder.currentOrThrow

            // This updates when the transition is done
            LaunchedEffect(Unit) {
                currentContentTransform?.targetContentZIndex = 0f

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

        screen.render {
            content(it)
        }
    }
}

/**
 * remember() that keeps the FIRST [onBack] for the given keys, so the handler
 * registration survives recompositions. Generic so the caller never has to name
 * the lambda's parameter type - it flows in by inference.
 */
@Composable
private fun <T : Any> rememberOnBack(vararg keys: Any?, onBack: T): T =
    remember(*keys) { onBack }

/**
 * Just an utility saver for the screens that should be disposed during a transition.
 */
private fun unexpectedScreenStateKeysQueueSaver(): Saver<MutableState<Set<String>>, List<String>> {
    return Saver(
        save = { it.value.toList() },
        restore = { mutableStateOf(it.toSet()) }
    )
}
