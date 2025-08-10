# ScreenModel

Vortex provides its own Multiplatform `ViewModel` called `ScreenModel`, which functions in the exact same way.

```kotlin
class HomeScreenModel : ScreenModel {
   
    init {
        screenModelScope.launch {
            // ..
        }
    }
    
    // Optional
    override fun onDispose() {
        // ...
    }
}
```

A `ScreenModel` can be bound to either the `Screen` it's in or a `Navigator`:

```kotlin
class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { HomeScreenModel() }
        // ...
        val navigator = LocalNavigator.currentOrThrow
        val navigatorScreenModel = navigator.rememberNavigatorScreenModel { HomeScreenModel() }
        // ...
    }
}
```

If you need to have multiple instances of the same `ScreenModel` for the same `Screen` or `Navigator`, you can add a tag
to differentiate them.

```kotlin
val screenModel = rememberScreenModel(tag = "CUSTOM_TAG") { HomeScreenModel() }
```

### Desktop Note

!!! note
    If you are targeting Desktop, you should provide the dependency `org.jetbrains.kotlinx:kotlinx-coroutines-swing`, the `screenModelScope` depends on `Dispatchers.Main` provided by this library on Desktop. We don't include it because this library is incompatible with IntelliJ Plugin, [see](https://youtrack.jetbrains.com/issue/IDEA-285839). If you are targeting Desktop for IntelliJ plugins, this library does not require to be provided.

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/screenModel/ScreenModelIntegration.kt)."

