package io.github.hristogochev.vortex.sample.multiplatform

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.navigator.Navigator

@Composable
fun SampleApplication() {
    Navigator(
        screen = BasicNavigationScreen(index = 0),
        defaultBackHandler = false
    )
}
