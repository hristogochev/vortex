package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Density
import io.github.hristogochev.vortex.screen.ScreenTransitionCancellable

public data class AndroidPredictiveBackTransitionCancellable(val density: Density) :
    ScreenTransitionCancellable {
    override val zIndex: Float? = -1f

    override val cancellableAnimationSpec: AnimationSpec<Float> = tween(
        durationMillis = 100,
        easing = LinearEasing
    )

    override fun enter(): EnterTransition = androidPredictiveBackEnterBackward(density)

    override fun exit(): ExitTransition = androidPredictiveBackExitBackward(density)
}