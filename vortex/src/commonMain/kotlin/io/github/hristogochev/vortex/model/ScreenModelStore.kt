package io.github.hristogochev.vortex.model

import androidx.compose.runtime.DisallowComposableCalls
import io.github.hristogochev.vortex.util.ThreadSafeMap
import kotlinx.coroutines.flow.MutableStateFlow

public object ScreenModelStore {

    @PublishedApi
    internal val screenModels: MutableMap<String, ScreenModel> = ThreadSafeMap()

    @PublishedApi
    internal val screenModelDependencies: MutableMap<String, Pair<Any, (Any) -> Unit>> =
        ThreadSafeMap()


    // The screen model may not be inserted in the map by the time we request a dependency, so we use this as an workaround
    @PublishedApi
    internal val lastScreenModelKey: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * Gets and optionally saves a screen model by key, the key is a screen/navigator key + something else
     */
    @PublishedApi
    internal inline fun <reified T : ScreenModel> getOrPut(
        key: String,
        factory: @DisallowComposableCalls () -> T,
    ): T {
        lastScreenModelKey.value = key
        return screenModels.getOrPut(key, factory) as T
    }

    /**
     * Gets and optionally saves a dependency for a screen model
     */
    internal inline fun <reified T : Any> getOrPutScreenModelDependency(
        screenModel: ScreenModel,
        name: String,
        noinline onDispose: @DisallowComposableCalls (T) -> Unit = {},
        noinline factory: @DisallowComposableCalls (String) -> T,
    ): T {
        val key = screenModels
            .firstNotNullOfOrNull {
                if (it.value == screenModel) {
                    it.key
                } else {
                    null
                }
            } ?: lastScreenModelKey.value
            ?.let { "$it:$name" }
        ?: "standalone:$name"

        return screenModelDependencies
            .getOrPut(key) {
                @Suppress("UNCHECKED_CAST")
                (factory(key) to onDispose) as Pair<Any, (Any) -> Unit>
            }
            .first as T
    }


    /**
     * Disposes of all screen models and their dependencies for a screen/navigator
     */
    public fun dispose(holderKey: String) {
        // Creates a copy of all screen model keys
        // Finds the ones starting with the holder key
        // Then disposes of all screen models with the keys that match
        screenModels.keys.toSet()
            .asSequence()
            .filter { it.startsWith(holderKey) }
            .forEach { key ->
                screenModels[key]?.onDispose()
                screenModels -= key
            }

        // Creates a copy of all screen model dependency keys
        // Finds the ones starting with the holder key
        // Then disposes of all screen model dependencies with the keys that match
        screenModelDependencies.keys.toSet()
            .asSequence()
            .filter { it.startsWith(holderKey) }
            .forEach { key ->
                screenModelDependencies[key]?.also { (scope, onDispose) ->
                    onDispose(scope)
                }
                screenModelDependencies -= key
            }
    }
}

