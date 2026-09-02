package io.github.hristogochev.vortex.sample.parcelableScreen

import androidx.compose.runtime.Composable
import io.github.hristogochev.vortex.navigator.Navigator

@Composable
fun ParcelableBasicNavigation(){
    Navigator(
        screen = ParcelableBasicScreen(
            parcelable = ParcelableBasicScreenContent(
                index = 0
            )
        ),
    )
}