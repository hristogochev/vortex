package io.github.hristogochev.vortex.util

public interface ThreadSafeSet<T> : MutableSet<T> {
    public companion object {

        public operator fun <T> invoke(initial: Collection<T>): ThreadSafeSet<T> {
            return getThreadSafeSet<T>().apply {
                if (initial.isNotEmpty()) addAll(initial)
            }
        }
    }
}

public expect fun <T> getThreadSafeSet(): ThreadSafeSet<T>
