# Nested navigation

There should be no issues if you need to set up some sort of nested navigation.<br>
Any navigator `N` with screen `B` created within screen `A` is `bound` to the lifecycle of screen `A`.<br>
If screen `A` happens to get disposed, navigator `N` and its screen `B` will also get disposed.

```kotlin
@Composable
fun App(){
    Navigator(ScreenA)
}

data object ScreenA : Screen {

    @Composable
    override fun Content() {
        Navigator(ScreenB)
    }
}

data object ScreenB : Screen {

    @Composable
    override fun Content() {
        // ...
    }
}
```

!!! note "You can call LocalNavigator.currentOrThrow.parent to access the navigator of the screen owning the current navigator."

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/nestedNavigation/NestedNavigation.kt)."

