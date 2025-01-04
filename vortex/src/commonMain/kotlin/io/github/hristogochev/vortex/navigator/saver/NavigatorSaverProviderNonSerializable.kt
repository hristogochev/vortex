package io.github.hristogochev.vortex.navigator.saver

import androidx.compose.runtime.saveable.SaverScope
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.util.ThreadSafeSet

/**
 * More relaxed navigator saver provider.
 *
 * Does not require all parameters and properties of every screen to be Serializable or Parcelable.
 *
 * DOES NOT SUPPORT PROCESS DEATH ON ANDROID!!!
 */
public data object NonSerializableNavigatorSaverProvider : NavigatorSaverProvider<String> {
    override fun provide(parent: Navigator?): NavigatorSaver<String> {
        return object : NavigatorSaver<String> {
            override fun SaverScope.save(value: Navigator): String {
                // We need to use `.toList()` to create a copy of the items and screen state keys,
                // otherwise their references will be used, which will not work
                val contentsMap = mapOf(
                    "items" to value.items.toList(),
                    "screenStateKeys" to value.getAllScreenStateKeys().toList()
                )

                // Save the current navigator contents into an outside map, using its key
                NonSerializableNavigatorsStore.navigators[value.key] = contentsMap

                // Only tell our saver about the navigator's key, since we manage the saving externally
                return value.key
            }

            override fun restore(value: String): Navigator? {
                // If there are no saved contents for the navigator by it's key,
                // forget the current saved one and recreate the navigator
                val navigatorContents = NonSerializableNavigatorsStore.navigators[value] ?: return@restore null

                // If any of the core components of a navigator are missing,
                // forget the current saved one and recreate the navigator
                @Suppress("UNCHECKED_CAST")
                val savedScreens =
                    navigatorContents["items"] as? List<Screen> ?: run {
                        NonSerializableNavigatorsStore.navigators.remove(value)
                        return@restore null
                    }

                @Suppress("UNCHECKED_CAST")
                val savedScreenStateKeys =
                    navigatorContents["screenStateKeys"] as? List<String> ?: run {
                        NonSerializableNavigatorsStore.navigators.remove(value)
                        return@restore null
                    }

                return Navigator(
                    initialScreens = savedScreens,
                    key = value,
                    parent = parent,
                    screenStateKeys = ThreadSafeSet(savedScreenStateKeys)
                )
            }

            override fun dispose(value: Navigator) {
                // When a navigator is disposed make sure to clean up the NavigatorsStore
                NonSerializableNavigatorsStore.navigators.remove(value.key)
            }
        }
    }
}

/**
 * Non-serializable navigators singleton storage.
 *
 * Gets reset on process death.
 */
public data object NonSerializableNavigatorsStore {
    // A mutable map of navigators, that takes the navigators' keys as keys,
    // and their screens and screenStateKeys as values
    val navigators: MutableMap<String, Map<String, Any>> = mutableMapOf()
}