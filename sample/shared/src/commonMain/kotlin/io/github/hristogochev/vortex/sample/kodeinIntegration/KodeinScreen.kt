package io.github.hristogochev.vortex.sample.kodeinIntegration

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.kodein.kodeinScreenModel
import io.github.hristogochev.vortex.sample.common.ListContent
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.uniqueScreenKey

class KodeinScreen(override val key: String = uniqueScreenKey()) : Screen {

    @Composable
    override fun Content() {
        val screenModel = kodeinScreenModel<KodeinScreenModel>()

        Column {
            Text("Hello kodein!")
            ListContent(screenModel.items)
        }
    }
}
