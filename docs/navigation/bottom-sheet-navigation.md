# BottomSheet navigation

The only difference between basic navigation and bottom sheet navigation is that any `Navigator` inside a `BottomSheet` needs to have its `disposeOnForgotten` flag set to `true`.
<br>The flag is used to correctly dispose of the Navigator once it exits the composition. Necessary in ModalBottomSheets.

```kotlin
@Composable
fun App() {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = {
                showBottomSheet = true
            }
        ) {
            Text(text = "Show BottomSheet")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
        ) {
            Navigator(FirstBottomSheetScreen, disposeOnForgotten = true)
        }
    }
}
```

!!! info "You can find source code for a working example [here](https://github.com/hristogochev/vortex/blob/main/samples/sample/src/commonMain/kotlin/io/github/hristogochev/vortex/sample/bottomSheetNavigation/BottomSheetNavigation.kt)."

