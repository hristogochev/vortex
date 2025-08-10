package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.bottomSheetNavigation.BottomSheetNavigation
import io.github.hristogochev.vortex.screen.Screen

data object BottomSheetNavigationScreen : Screen {
    @Composable
    override fun Content() {
        BottomSheetNavigation()
    }
}