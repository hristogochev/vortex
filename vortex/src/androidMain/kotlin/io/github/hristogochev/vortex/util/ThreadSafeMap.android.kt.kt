package io.github.hristogochev.vortex.util

import java.util.concurrent.ConcurrentHashMap

public class DesktopThreadSafeMap<K,V>: MutableMap<K, V> by ConcurrentHashMap(), ThreadSafeMap<K, V>

public actual fun <K, V> getThreadSafeMap(): ThreadSafeMap<K, V> {
    return DesktopThreadSafeMap()
}