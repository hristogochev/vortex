# Basic navigation

To set up basic navigation, simply implement the `Screen` interface for a data class or object.<br>
The `Content()` function of a `Screen` is used for displaying its contents.

```kotlin
data object HomeScreen : Screen {

    @Composable
    override fun Content() {
        // ...
    }
}

data class DetailsScreen(val id: Long) : Screen {

    @Composable
    override fun Content() {
        // ...
    }
}
```

Then anywhere in your composable logic call `Navigator()`, passing in its initial screens.<br>
A navigator is used to manage the screens you create.<br>
You can `push`, `pop` or `replace` screens within it.

```kotlin
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
                navigator.push(DetailsScreen(1))
                // navigator.replace(DetailsScreen(1)) is also possible
                // "replace" removes the current screen and replaces it with the one specified.
            }
        ) {
            Text(text = "Show details")
        }
    }
}
data class DetailsScreen(val id: Long) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        Button(
            onClick = {
                navigator.pop()
                // If we invoked "replace" on the current screen with this one instead of pushing, "pop" wouldn't do anything.
            }
        ) {
            Text(text = "Go back")
        }
    }
}
```

!!! note "LocalNavigator is a function that returns the navigator that owns the current screen."

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/basicNavigation/BasicNavigation.kt)."




