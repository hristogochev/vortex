package io.github.hristogochev.vortex.sample.app.screen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.sample.stateStack.StateStackExample
import io.github.hristogochev.vortex.screen.Screen

data object StateStackExampleScreen : Screen {
    @Composable
    override fun Content() {
        StateStackExample()
    }
}