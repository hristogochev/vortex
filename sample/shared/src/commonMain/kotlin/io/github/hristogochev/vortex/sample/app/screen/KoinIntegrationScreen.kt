package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.koinIntegration.KoinIntegration
import io.github.hristogochev.vortex.screen.Screen

data object KoinIntegrationScreen : Screen {
    @Composable
    override fun Content() {
        KoinIntegration()
    }
}