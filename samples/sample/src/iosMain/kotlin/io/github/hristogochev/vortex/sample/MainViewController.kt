package io.github.hristogochev.vortex.sample

import androidx.compose.ui.window.ComposeUIViewController
import io.github.hristogochev.vortex.sample.app.App
import io.github.hristogochev.vortex.sample.kodeinIntegration.initKodein
import io.github.hristogochev.vortex.sample.koinIntegration.initKoin
import org.kodein.di.compose.withDI
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val di = initKodein()
    initKoin()
    return ComposeUIViewController {
        withDI(di){
            App()
        }
    }
}
