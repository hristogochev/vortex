package io.github.hristogochev.vortex.util

import kotlinx.atomicfu.locks.SynchronizedObject

public actual fun <T> getThreadSafeSet(): ThreadSafeSet<T> {
    return NativeThreadSafeSet()
}

public class NativeThreadSafeSet<T>(
    syncObject: SynchronizedObject,
    delegate: MutableSet<T>,
) : MutableSet<T>, ThreadSafeSet<T>, ThreadSafeMutableCollection<T>(syncObject, delegate) {
    public constructor() : this(delegate = mutableSetOf())
    public constructor(delegate: MutableSet<T>) : this(SynchronizedObject(), delegate)
}
