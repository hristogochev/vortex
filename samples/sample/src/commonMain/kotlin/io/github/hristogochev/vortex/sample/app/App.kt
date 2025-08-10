package io.github.hristogochev.vortex.sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.sample.app.screen.BasicNavigationScreen
import io.github.hristogochev.vortex.sample.app.screen.BottomSheetNavigationScreen
import io.github.hristogochev.vortex.sample.app.screen.KodeinIntegrationScreen
import io.github.hristogochev.vortex.sample.app.screen.KoinIntegrationScreen
import io.github.hristogochev.vortex.sample.app.screen.NestedNavigationIntegrationScreen
import io.github.hristogochev.vortex.sample.app.screen.ParcelableBasicNavigationScreen
import io.github.hristogochev.vortex.sample.app.screen.ScreenModelIntegrationScreen
import io.github.hristogochev.vortex.sample.app.screen.StateStackExampleScreen
import io.github.hristogochev.vortex.sample.app.screen.TabNavigationScreen
import io.github.hristogochev.vortex.sample.app.screen.TransitionsExampleScreen
import io.github.hristogochev.vortex.screen.CurrentScreen
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.util.currentOrThrow

@Composable
fun App() {
    Navigator(screen = CombinedSampleScreen()) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta),
                    onClick = {
                        it.popUntilRoot()
                    }) {
                    Text("Back to start")
                }
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                CurrentScreen(it)
            }
        }
    }
}


data class CombinedSampleScreen(override val key: String = "Root screen") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                StartSampleButton("SnapshotStateStack") {
                    navigator.push(StateStackExampleScreen)
                }
                StartSampleButton("Basic Navigation") {
                    navigator.push(BasicNavigationScreen)
                }
                StartSampleButton("Basic Navigation with Parcelable") {
                    navigator.push(ParcelableBasicNavigationScreen)
                }
                StartSampleButton("Tab Navigation") {
                    navigator.push(TabNavigationScreen)
                }
                StartSampleButton("BottomSheet Navigation") {
                    navigator.push(BottomSheetNavigationScreen)
                }
                StartSampleButton("Nested Navigation") {
                    navigator.push(NestedNavigationIntegrationScreen)
                }
                StartSampleButton("ScreenModel") {
                    navigator.push(ScreenModelIntegrationScreen)
                }
                StartSampleButton("Screen Transition") {
                    navigator.push(TransitionsExampleScreen)
                }
                StartSampleButton("Koin Integration") {
                    navigator.push(KoinIntegrationScreen)
                }
                StartSampleButton("Kodein Integration") {
                    navigator.push(KodeinIntegrationScreen)
                }
            }
        }
    }

    @Composable
    private fun StartSampleButton(
        text: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text = text)
        }
    }
}

