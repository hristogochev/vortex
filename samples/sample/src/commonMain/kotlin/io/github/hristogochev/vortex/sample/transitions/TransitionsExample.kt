package io.github.hristogochev.vortex.sample.transitions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.screen.CurrentScreen
import io.github.hristogochev.vortex.transitions.FadeTransition
import io.github.hristogochev.vortex.transitions.SlideTransition
import kotlin.random.Random

@Composable
fun TransitionsExample() {
    Navigator(
        screen = BaseTransitionScreen(transitionType = "Default navigator transition", index = 0),
    ) { navigator ->
        Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().systemBarsPadding()) {
            CurrentScreen(
                navigator = navigator,
                defaultOnScreenAppearTransition = SlideTransition.Horizontal.Appear,
                defaultOnScreenDisappearTransition = SlideTransition.Horizontal.Disappear,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            navigator.push(
                                BaseTransitionScreen(
                                    "Fade transition",
                                    index = navigator.items.size,
                                    onAppearTransition = FadeTransition,
                                    onDisappearTransition = FadeTransition
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Fade")
                    }

                    Button(
                        onClick = {
                            navigator.push(
                                BaseTransitionScreen(
                                    "Slide in vertically transition",
                                    index = navigator.items.size,
                                    onAppearTransition = SlideTransition.Vertical.Appear,
                                    onDisappearTransition = SlideTransition.Vertical.Disappear
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "SlideInVertically")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            navigator.push(
                                BaseTransitionScreen(
                                    transitionType = "Default navigator transition",
                                    index = navigator.items.size
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Default")
                    }

                    Button(
                        onClick = { navigator.pop() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Pop")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = {
                            navigator.replace(
                                BaseTransitionScreen(
                                    transitionType = "Default navigator transition",
                                    index = Random.nextInt()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Replace")
                    }

                    Button(
                        onClick = {
                            navigator.replaceAll(
                                BaseTransitionScreen(
                                    transitionType = "Default navigator transition",
                                    index = Random.nextInt()
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "ReplaceAll")
                    }
                }
            }
        }
    }
}