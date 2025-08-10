import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.hristogochev.vortex.sample.app.App
import io.github.hristogochev.vortex.sample.kodeinIntegration.initKodein
import io.github.hristogochev.vortex.sample.koinIntegration.initKoin
import org.jetbrains.skiko.wasm.onWasmReady
import org.kodein.di.compose.withDI

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val di = initKodein()
    initKoin()
    onWasmReady {
        CanvasBasedWindow("Vortex Sample") {
            withDI(di){
                App()
            }
        }
    }
}
