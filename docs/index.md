# Overview

![Logo](img/logo_smaller.png)

### [Vortex](https://github.com/hristogochev/vortex): Spin up your Compose Navigation

Stability-focused Compose Multiplatform Navigation Library, fork of [Voyager](https://github.com/adrielcafe/voyager).

Vortex maintains nearly the same [API](https://hristogochev.github.io/vortex) as Voyager, ensuring seamless integration:

```kotlin
class HomeScreenModel : ScreenModel {
    // ...
}

data object HomeScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel<HomeScreenModel>()
        // ...
    }
}


@Composable
fun App() {
    Navigator(HomeScreen)
}
```

### **Features**
- [Supported platforms](setup.md): Android, iOS, Desktop, Wasm, JS
* [Basic navigation](navigation/index.md)
* [Nested navigation](navigation/nested-navigation.md)
* [Bottom sheet navigation](navigation/bottom-sheet-navigation.md)
* [Advanced navigation](navigation/advanced-navigation.md)
* [Tab navigation](navigation/tab-navigation.md) (like bottom navigation)
* [ScreenModel](screenmodel/index.md) (Multiplatform ViewModel)
* [Transitions](transitions.md) per screen
* [State restoration](state-restoration.md) after Activity recreation
* [Callbacks](lifecycle.md) for screen creation and disposal
* [Deep linking](deep-links.md) support
* State-aware [Stack API](stack-api.md)

### **Differences with Voyager**

##### Navigation
* **Navigator-Screen Linking**:<br>Navigators are now directly linked to their parent screen instead of their parent navigator.  
* **Automatic Disposal**:<br>When a screen is disposed, all navigators within it are automatically disposed.  
* **Flexible Bottom Sheet Navigation**:<br>Bottom sheet navigation can now be used in any bottom sheet.
* **Simplified Bottom Sheet Navigators**:<br> Bottom sheet navigators have been streamlined into simple navigators with a `disposeOnForgotten` flag.

##### Transitions
* **Navigator Default Transitions**:<br>You can specify default navigator overridable screen appear and disappear transitions.
* **Runtime Appear Transition**:<br>You can specify the appear transition for a screen at runtime.  
* **Runtime Disappear Transition**:<br>You can specify the disappear transition for a screen at runtime.
* **Experimental iOS-swipe Transition**:<br>An iOS-swipe back transition that allows swiping to go back.

##### Lifecycle
* **Screen Disposable Effect**:<br>A new screen disposable effect is available, tied to the lifecycle of the screen.
* **Reliable Application Lifecycle Owner**:<br>You can now reliably access the application's multiplatform lifecycle owner without risk of failure to update.  

##### Missing features
* **Android specific state management**:<br>No support for Android-specific APIs, such as LiveData, Hilt, and RxJava.
* **No screen-specific lifecycle owners**:<br>Screens no longer override the default LocalLifecycleOwner.
* **No screen registry**:<br>Unlike Voyager, Vortex does not provide a screen registry.

### Acknowledgments

* Big thanks to [Adriel Café](https://adriel.cafe/) and all contributors of the original [Voyager](https://github.com/adrielcafe/voyager) library.