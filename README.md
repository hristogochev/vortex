[![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=blue&metadataUrl=https://repo.maven.apache.org/maven2/io/github/hristogochev/vortex/maven-metadata.xml&style=for-the-badge)](https://repo.maven.apache.org/maven2/io/github/hristogochev/vortex/)
[![Android API](https://img.shields.io/badge/api-23%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=23)
[![kotlin](https://img.shields.io/github/languages/top/hristogochev/vortex.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?style=for-the-badge)](https://ktlint.github.io/)
[![License MIT](https://img.shields.io/github/license/hristogochev/vortex.svg?style=for-the-badge&color=orange)](LICENSE.md)

<div align="center">
    <br>
    <img height="150" style="padding-bottom: 4px;" src="docs/img/logo_vortex.png" alt="Logo"/>
    <h1>
        <a href="https://hristogochev.github.io/vortex">Vortex</a>: Spin up your Compose Navigation
    </h1>
</div>

Stability-focused Compose Multiplatform Navigation Library, fork
of [Voyager](https://github.com/adrielcafe/voyager).

Vortex maintains nearly the same [API](https://hristogochev.github.io/vortex) as Voyager, ensuring
seamless integration:

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

### Documentation

See the [project website](https://hristogochev.github.io/vortex) for documentation and APIs.

### **Features**

* [Supported platforms](https://hristogochev.github.io/vortex/setup): Android, iOS, Desktop, Wasm
  and JS
* [Basic navigation](https://hristogochev.github.io/vortex/navigation)
* [Nested navigation](https://hristogochev.github.io/vortex/navigation/nested-navigation)
* [Bottom sheet navigation](https://hristogochev.github.io/vortex/navigation/bottom-sheet-navigation)
* [Advanced navigation](https://hristogochev.github.io/vortex/navigation/advanced-navigation)
* [Tab navigation](https://hristogochev.github.io/vortex/navigation/tab-navigation) (like bottom
  navigation)
* [ScreenModel](https://hristogochev.github.io/vortex/screenmodel) (Multiplatform ViewModel)
* [Transitions](https://hristogochev.github.io/vortex/transitions) per screen
* [State restoration](https://hristogochev.github.io/vortex/state-restoration) after Activity
  recreation
* [Callbacks](https://hristogochev.github.io/vortex/lifecycle) for screen creation and disposal
* [Deep linking](https://hristogochev.github.io/vortex/deep-links) support
* State-aware [Stack API](https://hristogochev.github.io/vortex/stack-api)

### Differences with Voyager

##### Navigation

* **Navigator-Screen Linking**:<br>Navigators are now directly linked to their parent screen instead
  of their parent
  navigator.
* **Automatic Disposal**:<br>When a screen is disposed, all navigators within it are automatically
  disposed.
* **Flexible Bottom Sheet Navigation**:<br>Bottom sheet navigation can now be used in any bottom
  sheet.
* **Simplified Bottom Sheet Navigators**:<br> Bottom sheet navigators have been streamlined into
  simple navigators with
  a `disposeOnForgotten` flag.

##### Transitions

* **Navigator Default Transitions**:<br>You can specify default navigator overridable screen appear
  and disappear
  transitions.
* **Runtime Appear Transition**:<br>You can specify the appear transition for a screen at runtime.
* **Runtime Disappear Transition**:<br>You can specify the disappear transition for a screen at
  runtime.

##### Lifecycle

* **Screen Disposable Effect**:<br>A new screen disposable effect is available, tied to the
  lifecycle of the screen.
* **Reliable Application Lifecycle Owner**:<br>You can now reliably access the application's
  multiplatform lifecycle
  owner without risk of failure to update.

##### Missing features

* **Android specific state management**:<br>No support for Android-specific APIs, such as LiveData,
  Hilt, and RxJava.
* **No screen-specific lifecycle owners**:<br>Screens no longer override the default
  LocalLifecycleOwner.
* **No screen registry**:<br>Unlike Voyager, Vortex does not provide a screen registry.

### Acknowledgments

* Big thanks to [Adriel Café](https://adriel.cafe/) and all contributors of the
  original [Voyager](https://github.com/adrielcafe/voyager) library.

### Samples

| [Basic nav.](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/basicNavigation) | [Tab nav.](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/tabNavigation) | [BottomSheet nav.](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/bottomSheetNavigation) |
|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![navigation-basic](docs/img/basic_nav.gif)                                                                                                       | ![navigation-tab](docs/img/tab_nav.gif)                                                                                                       | ![navigation-bottom-sheet](docs/img/bottom_sheet_nav.gif)                                                                                                     |

| [Nested nav.](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/nestedNavigation) | [ScreenModel](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/screenModel) | [Stack API](https://github.com/hristogochev/vortex/tree/main/samples/android/src/main/java/io/github/hristogochev/vortex/sample/stateStack) |
|-----------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| ![navigation-nested](docs/img/nested_nav.gif)                                                                                                       | ![navigation-screenmodel](docs/img/screen_model.gif)                                                                                           | ![navigation-stack](docs/img/stack.gif)                                                                                                     |