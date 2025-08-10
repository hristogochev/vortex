# Koin integration

!!! success
    To use the `koinScreenModel` you should first import `io.github.hristogochev:vortex-koin` (see [Setup](../setup.md)).

Declare your `ScreenModel`s using the `factory` component.

```kotlin
val homeModule = module {
    factory { HomeScreenModel() }
}
```

Call `koinScreenModel()` or `koinNavigatorScreenModel()` to get a new instance.

```kotlin
class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        // ...
        val navigator = LocalNavigator.currentOrThrow
        val navigatorScreenModel = navigator.koinNavigatorScreenModel<HomeScreenModel>()
        // ...
    }
}
```


!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/koinIntegration/KoinIntegration.kt)."

