# State restoration

The default state restoration mechanism of Vortex requires every instance of `Screen` to be savable
in
a [Bundle](https://developer.android.com/guide/components/activities/parcelables-and-bundles),
therefore all params and
properties of your `Screen` implementations should be either `Java Serializable` or `Parcelable`.
Otherwise, your app
will crash upon attempting to restore its state.

Keep in mind that `Parcelables` are not `Java Serializable` by default and `Java Serializables` are
not `Parcelable` by
default.

### Java Serializable

```kotlin
// ✔️ DO
data class Post(/*...*/) : java.io.Serializable

data class ValidScreen(
    val userId: Long, // Built-in serializable types
    val post: Post // Your own serializable types
) : Screen {

    // Serializable properties
    val tag = "ValidScreen"

    // Lazily initialized serializable types
    val randomId by lazy { UUID.randomUUID() }

    // ...
}

// 🚫 DON'T
class Post(/*...*/)

data class InvalidScreen(
    val context: Context, // Built-in non-serializable types
    val post: Post, // Your own non-serializable types
    val parcelable: SomeParcelable // Android Parcelable is not Java Serializable by default
) : Screen {

    // Non-serializable properties
    val postService = PostService()

    // ...
}
```

### Android Parcelables

```kotlin
// ✔️ DO
@Parcelize
data class Post(/*...*/) : Parcelable

@Parcelize
data class ValidScreen(
    val post: Post // Your own parcelable types
) : Screen, Parcelable {
    // ...
}

// 🚫 DON'T
class Post(/*...*/)

@Parcelize
data class InvalidScreen(
    val context: Context, // Built-in non-parcelable types
    val post: Post, // Your own non-parcelable types
    val serializable: SomeSerializable // Java Serializable are not Android Parcelable by default
) : Screen, Parcelable {
    // ...
}
```

#### Enforcing Android Parcelable on your screens

You can build your own Screen type for enforcing in at compile time that all yours screens should be
Parcelable by
creating an interface that implement Parcelable.

```kotlin
interface ParcelableScreen : Screen, Parcelable

// Compile
@Parcelize
data class Post(/*...*/) : Parcelable

@Parcelize
data class ValidScreen(
    val post: Post
) : ParcelableScreen {
    // ...
}

// Not compile
data class Post(/*...*/)

@Parcelize
data class ValidScreen(
    val post: Post
) : ParcelableScreen {
    // ...
}
```

### Multiplatform state restoration

When working in a Multiplatform project you may need a common `Java Serializable` or `Parcelable`
interface/annotation,
you can create one like this:

```kotlin
// commonMain - module core
expect interface JavaSerializable

// androidMain - module core
actual typealias JavaSerializable = java.io.Serializable

// non AndroidMain (ios, web, etc) - module core
actual interface JavaSerializable
```

### Custom state restoration mechanism

You can create your own state restoration mechanism by inheriting from
the [NavigatorSaverProvider](https://github.com/hristogochev/vortex/blob/main/vortex/src/commonMain/kotlin/io/github/hristogochev/vortex/navigator/saver/NavigatorSaverProvider.kt)
interface into a data object.

Keep in mind that a `Navigator` needs it's `key`, `screens` and `screenStateKeys` upon restoration.

You also need to pass it it's `parent` reference, which is conveniently accessible to you upon
implementing the interface.

You can find an example of implementing a custom navigator saver provider [here](https://github.com/hristogochev/vortex/blob/main/vortex/src/commonMain/kotlin/io/github/hristogochev/vortex/navigator/saver/NavigatorSaverProviderNonSerializable.kt).
This example custom navigator saver provider is based on [kevinvanmierlo's solution](https://github.com/hristogochev/vortex/issues/1) for restoring screens with non-serializable parameters and properties (with the drawback that process death on Android stops being supported).

Once you have implemented your custom navigator saver provider you can apply it like this:
```kotlin
@Composable
fun App() {
    // This is at the top of our root navigator,
    // but you can also have different navigator saver providers for each Navigator
    CompositionLocalProvider(
        LocalNavigatorSaverProvider provides CustomNavigatorSaverProvider,
    ) {
        Navigator(HomeScreen)
    }
}
```

### Identifying screens

The `Screen` interface has a `key` property that defines it in each `Navigator`.<br>
The default key for a `Screen` is its name. You can override it to set your own key.

```kotlin
data class HomeScreen(
    override val key: String = "CUSTOM_KEY"
) : Screen {

    @Composable
    override fun Content() {
        // ...
    }
}
```

Vortex also has a `uniqueScreenKey` function, that generates a random key.

```kotlin
override val key = uniqueScreenKey()
```

!!! warning
    You should **always** set your own key, if the screen is used multiple times in the same
    `Navigator`, or is
    an [anonymous](https://kotlinlang.org/docs/object-declarations.html#object-expressions)
    or [local](https://kotlinlang.org/spec/declarations.html#local-class-declaration) class.

### Ignoring process death on Android

If you don't wish to support process death restoration on Android, you can add this to your Activity:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (savedInstanceState != null) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
        return
    }
    
    // ...
}
```


