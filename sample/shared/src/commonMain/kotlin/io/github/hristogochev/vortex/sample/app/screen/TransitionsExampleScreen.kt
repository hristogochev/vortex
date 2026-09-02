package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.transitions.TransitionsExample
import io.github.hristogochev.vortex.screen.Screen

data object TransitionsExampleScreen : Screen {
    @Composable
    override fun Content() {
        TransitionsExample()
    }
}