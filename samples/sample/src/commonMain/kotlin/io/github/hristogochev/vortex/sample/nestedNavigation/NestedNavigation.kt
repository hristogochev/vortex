package io.github.hristogochev.vortex.sample.nestedNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.annotation.ExperimentalVortexApi
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.basicNavigation.BasicScreen
import io.github.hristogochev.vortex.screen.CurrentScreen
import io.github.hristogochev.vortex.screen.CurrentScreenPredictiveBack
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.transitions.AndroidSlideTransitionTransitionPredictiveBack


@OptIn(ExperimentalVortexApi::class)
@Composable
fun NestedNavigationIntegration() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Blue)) {
        Navigator(screens = listOf(Screen0, ScreenA)) {
            val density = LocalDensity.current
            CurrentScreenPredictiveBack(
                it,
                defaultPredictiveBackTransition = AndroidSlideTransitionTransitionPredictiveBack(
                    density
                )
            )
        }
    }
}

data object Screen0 : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Cyan))
    }
}

data object ScreenA : Screen {

    @OptIn(ExperimentalVortexApi::class)
    @Composable
    override fun Content() {
        Column(modifier = Modifier.fillMaxSize().background(Color.Magenta)) {
            Box(modifier = Modifier.weight(0.5f).fillMaxWidth().background(Color.Green))
            Box(modifier = Modifier.weight(0.5f).fillMaxWidth().background(Color.Red)) {
                Navigator(screens = listOf(ScreenB, ScreenC)) {
                    val density = LocalDensity.current
                    CurrentScreenPredictiveBack(
                        it,
                        defaultPredictiveBackTransition = AndroidSlideTransitionTransitionPredictiveBack(
                            density
                        )
                    )
                }
            }
        }
    }
}

data object ScreenB : Screen {

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
    }
}

data object ScreenC : Screen {

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(Color.Yellow))
    }
}

@Deprecated("The old way of showcasing nested navigation")
@Composable
fun NestedNavigationIntegrationOld() {
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
