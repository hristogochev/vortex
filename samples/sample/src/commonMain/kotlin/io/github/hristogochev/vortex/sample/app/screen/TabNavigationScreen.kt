package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.tabNavigation.TabNavigation
import io.github.hristogochev.vortex.screen.Screen

data object TabNavigationScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigation()
    }
}
