# Transitions


### Default transitions

You can specify the default `appear` and `disappear` transitions for every `Screen` in a `Navigator`.

Vortex comes bundled with a set of a few transitions you can use for your apps.<br>You can check them out [here](https://github.com/hristogochev/vortex/tree/main/vortex/src/commonMain/kotlin/io/github/hristogochev/vortex/transitions).

In this example we will be using [FadeTransition](https://github.com/hristogochev/vortex/blob/main/vortex/src/commonMain/kotlin/io/github/hristogochev/vortex/transitions/FadeTransition.kt).

```kotlin hl_lines="6 7"
@Composable
fun App(){
    Navigator(HomeScreen) { navigator ->
        CurrentScreen(
            navigator = navigator,
            defaultOnScreenAppearTransition = FadeTransition,
            defaultOnScreenDisappearTransition = FadeTransition
        )
    }
}
```

This will make it so that the default fade transition will play everytime a new screen is pushed or popped from the navigator.

### Per screen transitions

You can make a screen appear with a specific transition:

```kotlin hl_lines="17 27"
@Composable
fun App(){
    Navigator(HomeScreen)
}

data object HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Button(
            onClick = {
                navigator.push(
                    DetailsScreen(
                        id = 1,
                        onAppearTransition = SlideTransition.Horizontal.Appear
                    )
                )
            }
        ) {
            Text(text = "Show details")
        }
    }
}

data class DetailsScreen(val id: Long, override val onAppearTransition: ScreenTransition?) :
    Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Button(
            onClick = {
                navigator.pop()
            }
        ) {
            Text(text = "Go back")
        }
    }
}
```

### iOS swipe transition

Vortex provides an experimental iOS-swipe back transition.

This solution is inspired by the ideas of multiple people in issue [#144](https://github.com/adrielcafe/voyager/issues/144) of Voyager.

The transition does not take in account the overrides set for screens in the current navigator.

```kotlin hl_lines="4"
@Composable
fun App(){
    Navigator(HomeScreen) { navigator ->
        CurrentScreenIOSSwipe(navigator)
    }
}
```

### Custom transitions

You can create your own transitions by implementing the `ScreenTransition` interface.

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/transitions/TransitionsExample.kt)."

