package io.github.hristogochev.vortex.navigator.saver

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.hristogochev.vortex.navigator.Navigator

public val LocalNavigatorSaverProvider: ProvidableCompositionLocal<NavigatorSaverProvider<*>> =
    staticCompositionLocalOf { SerializableNavigatorSaverProvider }

/**
 * The navigator saver provider template.
 *
 * Use this to create your own navigator saver provider.
 */
public interface NavigatorSaverProvider<Saveable : Any> {
    /**
     * Provide the saver instance
     */
    public fun provide(parent: Navigator?): NavigatorSaver<Saveable>
}




