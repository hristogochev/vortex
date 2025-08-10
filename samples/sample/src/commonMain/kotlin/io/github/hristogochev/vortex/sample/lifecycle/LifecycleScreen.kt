package io.github.hristogochev.vortex.sample.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.ScreenDisposableEffect

data object LifecycleScreen : Screen {

    @Composable
    override fun Content() {

        ScreenDisposableEffect {
            println("Created lifecycle screen")
            onDispose {
                println("Disposed of lifecycle screen")
            }
        }

        val counter = remember { 1 }

            ScreenDisposableEffect(counter) {
                println("Counter: $counter")
                onDispose {
                    println("Counter changed")
                }
            }

        val lifecycleOwner = LocalLifecycleOwner.current

        ScreenDisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        println("On create")
                    }

                    Lifecycle.Event.ON_START -> {
                        println("On start")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        println("On resume")
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        println("On pause")
                    }

                    Lifecycle.Event.ON_STOP -> {
                        println("On stop")
                    }

                    Lifecycle.Event.ON_DESTROY -> {
                        println("On destroy")
                    }

                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        // ...
    }
}