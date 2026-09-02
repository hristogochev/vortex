package io.github.hristogochev.vortex.sample.basicNavigation

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.navigator.Navigator

@Composable
fun BasicNavigation() {
    Navigator(
        screen = BasicScreen(index = 0),
    )
}