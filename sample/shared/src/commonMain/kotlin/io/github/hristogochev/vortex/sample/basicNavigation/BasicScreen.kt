package io.github.hristogochev.vortex.sample.basicNavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.navigator.LocalNavigator
import io.github.hristogochev.vortex.screen.Screen
import io.github.hristogochev.vortex.screen.ScreenDisposableEffect
import io.github.hristogochev.vortex.screen.uniqueScreenKey
import io.github.hristogochev.vortex.util.currentOrThrow

data class BasicScreen(
    val index: Int,
    val wrapContent: Boolean = false,
    override val key: String = uniqueScreenKey(),
) : Screen {


    @Composable
    override fun Content() {
        ScreenDisposableEffect {
            println("Navigator: Start screen #$index")
            onDispose {
                println("Navigator: Dispose screen #$index")
            }
        }

        val navigator = LocalNavigator.currentOrThrow

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.let{
                if (wrapContent) {
                    it.padding(vertical = 16.dp).wrapContentHeight()
                } else {
                    it.fillMaxSize()
                }
            }
        ) {
            Text(
                text = "Screen #$index",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Button(
                    enabled = navigator.canPop,
                    onClick = navigator::pop,
                    modifier = Modifier.weight(.5f)
                ) {
                    Text(text = "Pop")
                }

                Spacer(modifier = Modifier.weight(.1f))

                Button(
                    onClick = { navigator.push(BasicScreen(index.inc(), wrapContent)) },
                    modifier = Modifier.weight(.5f)
                ) {
                    Text(text = "Push")
                }

                Spacer(modifier = Modifier.weight(.1f))

                Button(
                    onClick = {
                        navigator.replace(
                            BasicScreen(
                                index.inc(),
                                wrapContent
                            )
                        )
                    },
                    modifier = Modifier.weight(.5f)
                ) {
                    Text(text = "Replace")
                }
            }

            val items = LocalNavigator.currentOrThrow.items
            val basicScreens by remember(items) {
                derivedStateOf {
                    items.filterIsInstance<BasicScreen>()
                }
            }
            LazyColumn(
                modifier = Modifier.height(100.dp)
            ) {
                items(basicScreens) { item ->
                    Text("Item #${item.index}")
                }
            }
        }
    }
}