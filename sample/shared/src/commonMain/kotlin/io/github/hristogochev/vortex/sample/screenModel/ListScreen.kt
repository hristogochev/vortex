package io.github.hristogochev.vortex.sample.screenModel

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.model.rememberScreenModel
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.sample.common.ListContent
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.uniqueScreenKey
import io.github.hristogochev.vortex.util.currentOrThrow

class ListScreen(override val key: String = uniqueScreenKey()) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ListScreenModel() }

        ListContent(screenModel.items, onClick = { index -> navigator.push(DetailsScreen(index)) })
    }
}
