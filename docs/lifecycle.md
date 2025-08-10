# Lifecycle

Inside a `Screen`, you can call `ScreenDisposableEffect` to execute a block of code 
the first time a `Screen` appears or any of its keys change.

You can also execute a block of code 
when the `Screen` gets disposed or any of its keys change using the `onDispose` callback.

```kotlin
data object HomeScreen : Screen {

    @Composable
    override fun Content() {

        ScreenDisposableEffect {
            println("Created home screen")
            onDispose {
                println("Disposed of home screen")
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
```

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/lifecycle/LifecycleScreen.kt)."
