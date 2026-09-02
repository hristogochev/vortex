package io.github.hristogochev.vortex.sample.tabNavigation.tabs

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.screen.uniqueScreenKey
import io.github.hristogochev.vortex.tab.Tab

object ProfileTab : Tab {


    override val key: String = uniqueScreenKey()

    override val index: UInt = 2u

    @Composable
    override fun Content() {
        TabContent()
    }
}
