package io.github.hristogochev.vortex.sample.screenModel

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.navigator.Navigator

@Composable
fun ScreenModelIntegration(){
    Navigator(ListScreen())
}