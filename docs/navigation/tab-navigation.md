# Tab navigation

!!! warning "Before reading this section it is advised to have read [Advanced navigation](advanced-navigation.md)."

To set up tab navigation, simply implement the `Tab` interface for a data class or object.

A `Tab` needs to have its own distinct index, and it's also a screen in itself.
For example, like a `Screen`, it also has a `Content` function that is used for displaying its contents.

```kotlin
data object HomeTab : Tab {
    override val index: UInt = 0u

    @Composable
    override fun Content() {
        // ...
    }
}

data object ProfileTab : Tab {
    override val index: UInt = 1u

    @Composable
    override fun Content() {
        // ...
    }
}
```

Tabs, just like screens need to exist within a `Navigator`. But instead of `CurrentScreen` they need to be rendered
with `CurrentTab`.
You would most likely want to manage tabs within a `Scaffold`).
Let's create a `Navigator` that manages tabs inside one.

```kotlin
@Composable
fun App(){
    Navigator(HomeTab) { navigator ->
        Scaffold(
            content = {
                CurrentTab(navigator)
            },
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(HomeTab)
                    TabNavigationItem(ProfileTab)
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val navigator = LocalNavigator.currentOrThrow

    val icon = when (tab.index) {
        0u -> rememberVectorPainter(Icons.Default.Home)
        1u -> rememberVectorPainter(Icons.Default.Person)
        else -> return
    }

    val title = when (tab.index) {
        0u -> "Home"
        2u -> "Profile"
        else -> return
    }

    NavigationBarItem(
        selected = navigator.current == tab,
        onClick = { navigator.current = tab },
        icon = { Icon(painter = icon, contentDescription = title) }
    )
}
```

!!! note "Tabs never get disposed until the screen owning the navigator they are in gets disposed."

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/tabNavigation/TabNavigation.kt)."
