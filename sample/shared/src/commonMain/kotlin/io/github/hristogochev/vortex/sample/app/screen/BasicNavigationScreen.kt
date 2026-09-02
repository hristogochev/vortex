package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.basicNavigation.BasicNavigation
import io.github.hristogochev.vortex.screen.Screen

data object BasicNavigationScreen : Screen {
    @Composable
    override fun Content() {
        BasicNavigation()
    }
}