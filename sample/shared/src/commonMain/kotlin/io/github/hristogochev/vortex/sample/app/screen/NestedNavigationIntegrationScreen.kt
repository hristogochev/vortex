package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.nestedNavigation.NestedNavigationIntegration
import io.github.hristogochev.vortex.screen.Screen

data object NestedNavigationIntegrationScreen : Screen {
    @Composable
    override fun Content() {
        NestedNavigationIntegration()
    }
}