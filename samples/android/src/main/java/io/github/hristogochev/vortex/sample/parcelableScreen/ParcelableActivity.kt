package io.github.hristogochev.vortex.sample.parcelableScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.hristogochev.vortex.navigator.Navigator

class ParcelableActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Navigator(
                screen = SampleParcelableScreen(
                    parcelable = ParcelableContent(
                        index = 0
                    )
                ),
            )
        }
    }
}
