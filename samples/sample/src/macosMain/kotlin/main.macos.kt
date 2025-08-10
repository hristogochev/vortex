import androidx.compose.ui.window.Window
import io.github.hristogochev.vortex.sample.app.App
import io.github.hristogochev.vortex.sample.kodeinIntegration.initKodein
import io.github.hristogochev.vortex.sample.koinIntegration.initKoin
import org.kodein.di.compose.withDI
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
    val di = initKodein()
    initKoin()
    NSApplication.sharedApplication()
    Window("Vortex") {
        withDI(di){
            App()
        }
    }
    NSApp?.run()
}
