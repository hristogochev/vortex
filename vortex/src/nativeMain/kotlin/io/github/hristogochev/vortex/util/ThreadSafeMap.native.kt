package io.github.hristogochev.vortex.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


public actual fun <K, V> getThreadSafeMap(): ThreadSafeMap<K, V> {
    return NativeThreadSafeMap()
}

public class NativeThreadSafeMap<K, V>(
    private val delegate: MutableMap<K, V>,
) : MutableMap<K, V>, ThreadSafeMap<K, V> {
    public constructor() : this(delegate = mutableMapOf())

    private val syncObject = SynchronizedObject()

    override val size: Int
        get() = synchronized(syncObject) { delegate.size }

    override fun containsKey(key: K): Boolean {
        return synchronized(syncObject) { delegate.containsKey(key) }
    }

    override fun containsValue(value: V): Boolean {
        return synchronized(syncObject) { delegate.containsValue(value) }
    }

    override fun get(key: K): V? {
        return synchronized(syncObject) { delegate[key] }
    }

    override fun isEmpty(): Boolean {
        return synchronized(syncObject) { delegate.isEmpty() }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = synchronized(syncObject) { NativeThreadSafeSet(syncObject, delegate.entries) }
    override val keys: MutableSet<K>
        get() = synchronized(syncObject) { NativeThreadSafeSet(syncObject, delegate.keys) }
    override val values: MutableCollection<V>
        get() = synchronized(syncObject) {
            ThreadSafeMutableCollection(
                syncObject,
                delegate.values
            )
        }

    override fun clear() {
        synchronized(syncObject) { delegate.clear() }
    }

    override fun put(key: K, value: V): V? {
        return synchronized(syncObject) { delegate.put(key, value) }
    }

    override fun putAll(from: Map<out K, V>) {
        synchronized(syncObject) { delegate.putAll(from) }
    }

    override fun remove(key: K): V? {
        return synchronized(syncObject) { delegate.remove(key) }
    }
}
