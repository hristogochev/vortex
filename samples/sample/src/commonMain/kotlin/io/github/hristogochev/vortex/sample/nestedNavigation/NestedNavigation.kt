package io.github.hristogochev.vortex.sample.nestedNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.basicNavigation.BasicScreen
import io.github.hristogochev.vortex.screen.CurrentScreen

@Composable
fun NestedNavigationIntegration(){
    LazyColumn {
        item {
            NestedNavigation(backgroundColor = Color.Gray) { firstNavigator ->
                CurrentScreen(firstNavigator)
                // Nested navigation can be inside a screen of NestedNavigation level 1,
                // here it's simply directly bound to `NestedNavigationScreen`
                NestedNavigation(backgroundColor = Color.LightGray) { secondNavigator ->
                    CurrentScreen(secondNavigator)
                    NestedNavigation(backgroundColor = Color.White) { thirdNavigator ->
                        CurrentScreen(thirdNavigator)
                        Button(
                            onClick = {
                                popUntilRootUntil(thirdNavigator) { navigator ->
                                    navigator.items.any { it.key == "Root screen" }
                                }
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(text = "Pop Until Root")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NestedNavigation(
    backgroundColor: Color,
    content: @Composable (navigator: Navigator) -> Unit = { CurrentScreen(it) },
) {
    Navigator(
        screen = BasicScreen(index = 0, wrapContent = true)
    ) { navigator ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .background(backgroundColor)
        ) {
            Text(
                text = "Level #${navigator.level()}",
                modifier = Modifier.padding(8.dp)
            )
            content(navigator)
        }
    }
}


// Like popUntilRoot but for this combined example
private tailrec fun popUntilRootUntil(navigator: Navigator, condition: (Navigator) -> Boolean) {
    navigator.popAll()

    val navigatorParent = navigator.parent
    if (navigatorParent != null && !condition(navigatorParent)) {
        popUntilRootUntil(navigatorParent, condition)
    }
}
