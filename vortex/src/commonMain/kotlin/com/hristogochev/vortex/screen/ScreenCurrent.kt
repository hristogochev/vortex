package com.hristogochev.vortex.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hristogochev.vortex.model.ScreenModelStore
import com.hristogochev.vortex.navigator.LocalNavigator
import com.hristogochev.vortex.navigator.LocalNavigatorStateHolder
import com.hristogochev.vortex.navigator.LocalScreenStateKey
import com.hristogochev.vortex.navigator.Navigator
import com.hristogochev.vortex.stack.StackEvent
import com.hristogochev.vortex.stack.isDisposableEvent
import com.hristogochev.vortex.util.currentOrThrow

/**
 *  Displays the current screen of a [Navigator].
 *
 *  Takes in a default transition for when a screen enters and leaves the visible area.
 *
 *  By default there is no transition.
 *
 *  Each [Screen] can have it's own transition for when it enters and leaves the visible area.
 */
@Composable
public fun CurrentScreen(
    navigator: Navigator,
    onScreenAppear: ScreenTransition? = null,
    onScreenDisappear: ScreenTransition? = null,
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

    AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = {

            val transition = when (navigator.lastEvent) {
                StackEvent.Pop -> initialState.onDisappear ?: onScreenDisappear
                else -> targetState.onAppear ?: onScreenAppear
            }

            ContentTransform(
                targetContentEnter = transition?.enter() ?: EnterTransition.None,
                initialContentExit = transition?.exit() ?: ExitTransition.None,
                targetContentZIndex = transition?.zIndex ?: 0f,
                sizeTransform = transition?.sizeTransform() ?: SizeTransform()
            )
        },
        contentAlignment = contentAlignment,
        contentKey = contentKey,
        modifier = modifier
    ) { screen ->
        if (this.transition.targetState == this.transition.currentState) {
            val stateHolder = LocalNavigatorStateHolder.currentOrThrow

            // This updates when the transition is done
            LaunchedEffect(Unit) {
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
 *  Displays the current screen of a [Navigator].
 *
 *  Forbids any transitions from showing.
 *
 *  Can be more efficient if you don't plan to use transitions.
 */
@Composable
public fun CurrentScreenNoTransitions(
    navigator: Navigator,
    content: @Composable (Screen) -> Unit = { it.Content() },
) {
    CurrentScreenNoTransitionsDisposable(navigator)

    navigator.lastItem.render {
        content(it)
    }
}

/**
 * Creates a listener that listens for screen stack changes and disposes of the screens that are no longer in the stack.
 */
@Composable
public fun CurrentScreenNoTransitionsDisposable(navigator: Navigator) {
    val stateHolder = LocalNavigatorStateHolder.currentOrThrow

    val oldScreenStateKeys = remember(navigator.items, navigator.key) {
        navigator.items.map { "${it.key}:${navigator.key}" }
    }

    DisposableEffect(oldScreenStateKeys) {
        onDispose {
            val currentScreenStateKeys = navigator.items.map { "${it.key}:${navigator.key}" }

            if (!navigator.lastEvent.isDisposableEvent()) {
                return@onDispose
            }

            val unexpectedScreenStateKeys =
                oldScreenStateKeys.filter { it !in currentScreenStateKeys }

            for (unexpectedScreenStateKey in unexpectedScreenStateKeys) {
                ScreenModelStore.dispose(unexpectedScreenStateKey)

                ScreenDisposableEffectStore.dispose(unexpectedScreenStateKey)

                stateHolder.removeState(unexpectedScreenStateKey)

                navigator.disassociateScreenStateKey(unexpectedScreenStateKey)
            }

            navigator.clearEvent()
        }
    }
}

/**
 * Renders the current screen, saving its state even if its no longer displayed.
 *
 * This function should only be used externally if you want to create a totally custom transition, e.g. iOS swipe.
 */
@Composable
public fun <T : Screen> T.render(content: @Composable (T) -> Unit = { it.Content() }) {

    val stateHolder = LocalNavigatorStateHolder.currentOrThrow
    val navigator = LocalNavigator.currentOrThrow

    val screenStateKey = remember(key, navigator.key) {
        "${key}:${navigator.key}"
    }

    navigator.associateScreenStateKey(screenStateKey)

    stateHolder.SaveableStateProvider(screenStateKey) {
        CompositionLocalProvider(
            LocalScreenStateKey provides screenStateKey
        ) {
            content(this)
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