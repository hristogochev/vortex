package io.github.hristogochev.vortex.navigator.saver

import androidx.compose.runtime.saveable.SaverScope
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.util.ThreadSafeSet


/**
 * The default navigator saver provider.
 *
 * Requires all parameters and properties of every screen to be Serializable or Parcelable.
 *
 * Supports process death on Android.
 */
public data object SerializableNavigatorSaverProvider : NavigatorSaverProvider<Map<String, Any>> {
    override fun provide(parent: Navigator?): NavigatorSaver<Map<String, Any>> {
        return object : NavigatorSaver<Map<String, Any>> {
            override fun SaverScope.save(value: Navigator): Map<String, Any> {
                // We need to use `.toList()` to create a copy of the items and screen state keys,
                // otherwise their references will be used, which will not work

                // We use map to save the current navigator
                // because maps are inherently serializable based on their contents
                return mapOf(
                    "key" to value.key,
                    "items" to value.items.toList(),
                    "screenStateKeys" to value.getAllScreenStateKeys().toList()
                )
            }

            override fun restore(value: Map<String, Any>): Navigator? {
                // If any of the core components of a navigator are missing,
                // forget the current saved one and recreate the navigator
                val savedKey = value["key"] as? String ?: return null

                @Suppress("UNCHECKED_CAST")
                val savedScreens =
                    value["items"] as? List<Screen> ?: return null

                @Suppress("UNCHECKED_CAST")
                val savedScreenStateKeys =
                    value["screenStateKeys"] as? List<String> ?: return null

                return Navigator(
                    initialScreens = savedScreens,
                    key = savedKey,
                    // We pass in the parent reference (it's kept up to date by Vortex)
                    parent = parent,
                    screenStateKeys = ThreadSafeSet(savedScreenStateKeys)
                )
            }
        }
    }
}