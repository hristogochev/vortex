package io.github.hristogochev.vortex.util

public interface ThreadSafeMap<K, V> : MutableMap<K, V> {
    public companion object {
        public operator fun <K, V> invoke(): ThreadSafeMap<K, V> {
            return getThreadSafeMap()
        }
    }
}

public expect fun <K, V> getThreadSafeMap(): ThreadSafeMap<K, V>
