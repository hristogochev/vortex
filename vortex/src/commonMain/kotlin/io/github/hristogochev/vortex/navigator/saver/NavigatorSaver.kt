package io.github.hristogochev.vortex.navigator.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import io.github.hristogochev.vortex.navigator.Navigator

/**
 * The navigator saver template.
 *
 * Use this to create your own navigator saver.
 */
public interface NavigatorSaver<Saveable : Any> : Saver<Navigator, Saveable> {
    override fun SaverScope.save(value: Navigator): Saveable?
    override fun restore(value: Saveable): Navigator?

    /**
     * Perform any necessary cleanups when a navigator instance is disposed
     */
    public fun dispose(value: Navigator) {}
}


