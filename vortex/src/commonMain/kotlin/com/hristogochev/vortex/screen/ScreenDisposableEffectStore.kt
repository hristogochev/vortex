package com.hristogochev.vortex.screen

import com.hristogochev.vortex.util.ThreadSafeMap
import com.hristogochev.vortex.util.ThreadSafeSet

/**
 * The internal storage for disposable effects
 */
internal object ScreenDisposableEffectStore {

    private val scheduledScreenDisposals =
        ThreadSafeMap<String, ThreadSafeSet<ScreenDisposableEffect>>()

    private val internalScreenDisposableEffectScope = ScreenDisposableEffectScope()

    /**
     * Schedule a disposable effect for a screen in the current composition
     */
    fun store(
        screenStateKey: String,
        effectKey: String,
        keys: Set<Any>,
        keysChanged: (Set<Any>) -> Boolean,
        effect: ScreenDisposableEffectScope.() -> ScreenDisposableEffectResult,
    ) {
        val disposableEffects = scheduledScreenDisposals.getOrPut(screenStateKey) {
            ThreadSafeSet(emptySet())
        }

        val isAlreadyScheduled = disposableEffects.find { it.uniqueKey == effectKey }

        if (isAlreadyScheduled != null) {
            if (keysChanged(isAlreadyScheduled.conditionalKeys)) {
                isAlreadyScheduled.onDispose.dispose()
                disposableEffects.remove(isAlreadyScheduled)
                val newOnDispose = effect(internalScreenDisposableEffectScope)
                ScreenDisposableEffect(
                    uniqueKey = effectKey,
                    registerOrderIndex = disposableEffects.size + 1,
                    conditionalKeys = keys,
                    onDispose = newOnDispose
                ).also { scope ->
                    disposableEffects.add(scope)
                }
            }
            return
        }

        val onDispose = effect(internalScreenDisposableEffectScope)

        ScreenDisposableEffect(
            uniqueKey = effectKey,
            registerOrderIndex = disposableEffects.size + 1,
            conditionalKeys = keys,
            onDispose = onDispose
        ).also { scope ->
            disposableEffects.add(scope)
        }
    }


    fun dispose(screenStateKey: String) {
        val screenDisposables = scheduledScreenDisposals.remove(screenStateKey) ?: return

        val effects = screenDisposables.sortedBy { it.registerOrderIndex }.reversed()

        for (effect in effects) {
            effect.onDispose.dispose()
        }
    }

    data class ScreenDisposableEffect(
        val uniqueKey: String,
        val registerOrderIndex: Int,
        val conditionalKeys: Set<Any>,
        val onDispose: ScreenDisposableEffectResult,
    )
}
