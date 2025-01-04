package io.github.hristogochev.vortex.util

public interface ThreadSafeList<T> : MutableList<T> {
    public companion object {
        public operator fun <T> invoke(): ThreadSafeList<T> {
            return getThreadSafeList()
        }
    }
}

public expect fun <T> getThreadSafeList(): ThreadSafeList<T>
