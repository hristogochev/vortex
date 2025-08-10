package io.github.hristogochev.vortex.sample.tabNavigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.tabNavigation.tabs.FavoritesTab
import io.github.hristogochev.vortex.sample.tabNavigation.tabs.HomeTab
import io.github.hristogochev.vortex.sample.tabNavigation.tabs.ProfileTab
import io.github.hristogochev.vortex.tab.CurrentTab
import io.github.hristogochev.vortex.tab.Tab
import io.github.hristogochev.vortex.util.currentOrThrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabNavigation(){
    Navigator(screen = HomeTab) { navigator ->
        Scaffold(
            topBar = {
                val title = when ((navigator.current as Tab).index) {
                    0u -> "Home"
                    1u -> "Favorites"
                    2u -> "Profile"
                    else -> "Unknown"
                }
                TopAppBar(
                    title = {
                        Text(text = title)
                    }
                )
            },
            content = { innerPadding ->
                CurrentTab(navigator)
            },
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(HomeTab)
                    TabNavigationItem(FavoritesTab)
                    TabNavigationItem(ProfileTab)
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val navigator = LocalNavigator.currentOrThrow

    val painter = when (tab.index) {
        0u -> rememberVectorPainter(Icons.Default.Home)
        1u -> rememberVectorPainter(Icons.Default.Favorite)
        2u -> rememberVectorPainter(Icons.Default.Person)
        else -> return
    }

    val title = when (tab.index) {
        0u -> "Home"
        1u -> "Favorites"
        2u -> "Profile"
        else -> return
    }

    NavigationBarItem(
        selected = navigator.current.key == tab.key,
        onClick = {
            navigator.current = tab
        },
        icon = {
            Icon(
                painter = painter,
                contentDescription = title
            )
        }
    )
}