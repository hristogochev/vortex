package io.github.hristogochev.vortex.util

import java.util.concurrent.CopyOnWriteArraySet

public class DesktopThreadSafeSet<T> : MutableSet<T> by CopyOnWriteArraySet(), ThreadSafeSet<T>

public actual fun <T> getThreadSafeSet(): ThreadSafeSet<T> {
    return DesktopThreadSafeSet()
}