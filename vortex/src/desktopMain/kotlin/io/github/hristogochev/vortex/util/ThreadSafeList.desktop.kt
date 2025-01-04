package io.github.hristogochev.vortex.util

import java.util.concurrent.CopyOnWriteArrayList

public class DesktopThreadSafeList<T> : MutableList<T> by CopyOnWriteArrayList(), ThreadSafeList<T>

public actual fun <T> getThreadSafeList(): ThreadSafeList<T> {
    return DesktopThreadSafeList()
}