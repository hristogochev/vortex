# Kodein integration

!!! success
    To use the `kodeinScreenModel` you should first import `io.github.hristogochev:vortex-kodein` (see [Setup](../setup.md)).

Declare your `ScreenModel`s using the `bindProvider` bind.

```kotlin
val homeModule = DI.Module(name = "home") {
    bindProvider { HomeScreenModel() } 
}
```

Call `kodeinScreenModel()` or `kodeinNavigatorScreenModel()` to get a new instance.

```kotlin
class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = kodeinScreenModel<HomeScreenModel>()
        // ...
        val navigator = LocalNavigator.currentOrThrow
        val navigatorScreenModel = navigator.kodeinNavigatorScreenModel<HomeScreenModel>()
        // ...
    }
}
```


!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/kodeinIntegration/KodeinIntegration.kt)."