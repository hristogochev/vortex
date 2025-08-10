package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.parcelableScreen.ParcelableBasicNavigation
import io.github.hristogochev.vortex.screen.Screen

data object ParcelableBasicNavigationScreen : Screen {
    @Composable
    override fun Content() {
        ParcelableBasicNavigation()
    }
}