package io.github.hristogochev.vortex.sample.tabNavigation.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.basicNavigation.BasicScreen
import io.github.hristogochev.vortex.screen.CurrentScreen
import io.github.hristogochev.vortex.screen.ScreenDisposableEffect
import io.github.hristogochev.vortex.tab.Tab
import io.github.hristogochev.vortex.transitions.SlideTransition
import io.github.hristogochev.vortex.util.currentOrThrow


@Composable
fun Tab.TabContent() {
    val tabTitle = when (index) {
        0u -> "Home"
        1u -> "Favorites"
        2u -> "Profile"
        else -> "Unknown"
    }

    ScreenDisposableEffect {
        println("Navigator: Start tab $tabTitle")
        onDispose {
            println("Navigator: Dispose tab $tabTitle")
        }
    }
    Navigator(BasicScreen(index = 0)) { navigator ->
        CurrentScreen(
            navigator = navigator,
            defaultOnScreenAppearTransition = SlideTransition.Horizontal.Appear,
            defaultOnScreenDisappearTransition = SlideTransition.Horizontal.Disappear,
        ) { screen ->
            Column {
                InnerTabNavigation()
                screen.Content()
                println("Navigator: Last Event: ${navigator.lastEvent}")
            }
        }
    }
}

@Composable
private fun InnerTabNavigation() {
    Row(
        modifier = Modifier.padding(16.dp)
    ) {
        TabNavigationButton(HomeTab)

        Spacer(modifier = Modifier.weight(.05f))

        TabNavigationButton(FavoritesTab)

        Spacer(modifier = Modifier.weight(.05f))

        TabNavigationButton(ProfileTab)
    }
}

@Composable
private fun RowScope.TabNavigationButton(
    tab: Tab,
) {
    val tabNavigator = LocalNavigator.currentOrThrow
    val tabTitle = when (tab.index) {
        0u -> "Home"
        1u -> "Favorites"
        2u -> "Profile"
        else -> "Unknown"
    }
    Button(
        enabled = tabNavigator.current.key != tab.key,
        onClick = { tabNavigator.replaceAll(tab) },
        modifier = Modifier.weight(1f)
    ) {
        Text(text = tabTitle)
    }
}
