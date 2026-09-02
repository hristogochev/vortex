package io.github.hristogochev.vortex.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.hristogochev.vortex.sample.app.App
import io.github.hristogochev.vortex.sample.kodeinIntegration.initKodein
import io.github.hristogochev.vortex.sample.koinIntegration.initKoin
import org.kodein.di.compose.withDI


fun main() {
    val di = initKodein()
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication) {
            withDI(di){
                App()
            }
        }
    }
}
