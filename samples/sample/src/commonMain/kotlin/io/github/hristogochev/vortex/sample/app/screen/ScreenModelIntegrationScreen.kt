package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.screenModel.ScreenModelIntegration
import io.github.hristogochev.vortex.screen.Screen

data object ScreenModelIntegrationScreen : Screen {
    @Composable
    override fun Content() {
        ScreenModelIntegration()
    }
}