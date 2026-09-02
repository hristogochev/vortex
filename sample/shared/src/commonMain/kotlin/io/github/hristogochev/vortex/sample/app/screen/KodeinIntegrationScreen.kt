package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.kodeinIntegration.KodeinIntegration
import io.github.hristogochev.vortex.screen.Screen

data object KodeinIntegrationScreen : Screen {
    @Composable
    override fun Content() {
        KodeinIntegration()
    }
}