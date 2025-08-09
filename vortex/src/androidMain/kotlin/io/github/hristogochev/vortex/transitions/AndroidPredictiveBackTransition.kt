package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.unit.Density
import io.github.hristogochev.vortex.screen.ScreenTransition

public sealed class AndroidPredictiveBackTransition : ScreenTransition {
    public data class Appear(val density: Density) : AndroidPredictiveBackTransition() {
        override fun enter(): EnterTransition = androidPredictiveBackEnterForward(density)
        override fun exit(): ExitTransition = androidPredictiveBackExitForward(density)
    }

    public data class Disappear(val density: Density) : AndroidPredictiveBackTransition() {
        override fun enter(): EnterTransition = androidPredictiveBackEnterBackward(density)
        override fun exit(): ExitTransition = androidPredictiveBackExitBackward(density)
    }
}