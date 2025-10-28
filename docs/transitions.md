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

### Predictive back transition

Vortex provides a predictive back transition when used with `CurrentScreenPredictiveBack`.

Here is an example with an iOS-swipe back transition.

```kotlin hl_lines="4 5 6 7 8 9"
@Composable
fun App(){
    Navigator(HomeScreen) { navigator ->
        CurrentScreenPredictiveBack(
            navigator = navigator,
            defaultPredictiveBackTransition = IOSSlideTransitionPredictiveBack,
            defaultOnScreenDisappearTransition = IOSSlideTransition.Disappear,
            defaultOnScreenAppearTransition = IOSSlideTransition.Appear
        )
    }
}
```

### Custom transitions

You can create your own transitions by implementing the `ScreenTransition` interface.

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/transitions/TransitionsExample.kt)."

