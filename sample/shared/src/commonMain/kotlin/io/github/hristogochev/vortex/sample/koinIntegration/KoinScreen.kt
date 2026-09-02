package io.github.hristogochev.vortex.sample.koinIntegration

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.koin.koinScreenModel
import io.github.hristogochev.vortex.sample.common.ListContent
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.uniqueScreenKey

class KoinScreen : Screen {

    override val key: String = uniqueScreenKey()

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<KoinScreenModel>()

        ListContent(screenModel.items)
    }
}
