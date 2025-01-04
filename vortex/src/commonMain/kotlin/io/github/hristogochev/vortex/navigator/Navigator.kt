package io.github.hristogochev.vortex.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.hristogochev.vortex.model.ScreenModelStore
import io.github.hristogochev.vortex.navigator.saver.LocalNavigatorSaverProvider
import io.github.hristogochev.vortex.screen.CurrentScreen
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.ScreenDisposableEffect
import io.github.hristogochev.vortex.screen.ScreenDisposableEffectStore
import io.github.hristogochev.vortex.stack.Stack
import io.github.hristogochev.vortex.stack.toMutableStateStack
import io.github.hristogochev.vortex.util.DisposableEffectUnlessChangingConfiguration
import io.github.hristogochev.vortex.util.ThreadSafeSet
import io.github.hristogochev.vortex.util.randomUuid


public val LocalNavigatorStateHolder: ProvidableCompositionLocal<SaveableStateHolder?> =
    staticCompositionLocalOf { null }


@Composable
private fun LocalNavigatorStateHolderProvider(content: @Composable () -> Unit) {
    val navigatorStateHolder = LocalNavigatorStateHolder.current

    if (navigatorStateHolder == null) {
        CompositionLocalProvider(
            LocalNavigatorStateHolder provides rememberSaveableStateHolder(),
        ) {
            content()
        }
    } else {
        content()
    }
}

public val LocalNavigator: ProvidableCompositionLocal<Navigator?> =
    staticCompositionLocalOf { null }

public val LocalScreenStateKey: ProvidableCompositionLocal<String?> =
    staticCompositionLocalOf { null }

public val Navigator.parentOrThrow: Navigator
    get() = parent ?: error("Navigator has no parent")

@Composable
public fun Navigator(
    screen: Screen,
    defaultBackHandler: Boolean = true,
    disposeOnForgotten: Boolean = false,
    content: @Composable (navigator: Navigator) -> Unit = { CurrentScreen(it) },
) {
    Navigator(
        screens = listOf(screen),
        defaultBackHandler = defaultBackHandler,
        disposeOnForgotten = disposeOnForgotten,
        content = content
    )
}

@Composable
public fun Navigator(
    screens: List<Screen>,
    defaultBackHandler: Boolean = true,
    disposeOnForgotten: Boolean = false,
    content: @Composable (navigator: Navigator) -> Unit = { CurrentScreen(it) },
) {
    require(screens.isNotEmpty()) { "Navigator must have at least one screen" }

    LocalNavigatorStateHolderProvider {
        val parent = LocalNavigator.current
        val parentUpdatedState by rememberUpdatedState(parent)

        val stateHolder =
            LocalNavigatorStateHolder.current ?: error("LocalNavigatorStateHolder not initialized")

        val navigatorSaverProvider = LocalNavigatorSaverProvider.current

        val navigatorSaver = remember(navigatorSaverProvider, parentUpdatedState) {
            navigatorSaverProvider.provide(parentUpdatedState)
        }

        val navigatorSaverDispose = navigatorSaver::dispose
        val navigatorSaverDisposeUpdatedState by rememberUpdatedState(navigatorSaverDispose)

        val navigator = rememberSaveable(saver = navigatorSaver) {
            val key = randomUuid()
            Navigator(screens, key, parentUpdatedState, ThreadSafeSet(emptySet()))
        }

        val navigatorUpdatedState by rememberUpdatedState(navigator)

        val localScreenStateKey = LocalScreenStateKey.current

        if (localScreenStateKey != null) {
            ScreenDisposableEffect(
                navigatorUpdatedState,
                navigatorSaverDisposeUpdatedState,
                disposeOnKeysChange = false
            ) {
                onDispose {
                    val screenStateKeys = navigatorUpdatedState.getAllScreenStateKeys()

                    for (screenStateKey in screenStateKeys) {

                        ScreenModelStore.dispose(screenStateKey)

                        ScreenDisposableEffectStore.dispose(screenStateKey)

                        stateHolder.removeState(screenStateKey)

                        navigatorUpdatedState.disassociateScreenStateKey(screenStateKey)
                    }
                    ScreenModelStore.dispose(navigatorUpdatedState.key)

                    navigatorUpdatedState.clearEvent()

                    navigatorSaverDisposeUpdatedState(navigatorUpdatedState)
                }
            }
        }

        if (localScreenStateKey == null || disposeOnForgotten) {
            DisposableEffectUnlessChangingConfiguration(Unit) {
                onDispose {
                    val screenStateKeys = navigatorUpdatedState.getAllScreenStateKeys()

                    for (screenStateKey in screenStateKeys) {

                        ScreenModelStore.dispose(screenStateKey)

                        ScreenDisposableEffectStore.dispose(screenStateKey)

                        stateHolder.removeState(screenStateKey)

                        navigatorUpdatedState.disassociateScreenStateKey(screenStateKey)
                    }
                    ScreenModelStore.dispose(navigatorUpdatedState.key)

                    navigatorUpdatedState.clearEvent()

                    navigatorSaverDisposeUpdatedState(navigatorUpdatedState)
                }
            }
        }

        if (defaultBackHandler) {
            NavigatorBackHandler(navigator)
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator
        ) {
            content(navigator)
        }
    }
}

public class Navigator(
    initialScreens: List<Screen>,
    public val key: String,
    public val parent: Navigator? = null,
    private val screenStateKeys: ThreadSafeSet<String>,
) : Stack<Screen> by initialScreens.toMutableStateStack(minSize = 1) {

    public fun level(): Int = parent?.level()?.inc() ?: 0

    public fun associateScreenStateKey(screenStateKey: String) {
        screenStateKeys += screenStateKey
    }

    public fun disassociateScreenStateKey(screenStateKey: String) {
        screenStateKeys -= screenStateKey
    }

    public fun getAllScreenStateKeys(): Set<String> {
        return this.screenStateKeys.toSet()
    }

    public fun popUntilRoot() {
        popUntilRoot(this)
    }

    private tailrec fun popUntilRoot(navigator: Navigator) {
        navigator.popAll()

        if (navigator.parent != null) {
            popUntilRoot(navigator.parent)
        }
    }
}

