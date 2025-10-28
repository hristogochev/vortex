package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import io.github.hristogochev.vortex.screen.ScreenTransitionPredictiveBack

public data object IOSSlideTransitionPredictiveBack : ScreenTransitionPredictiveBack {
    override val zIndex: Float? = -1f

    override val cancelAnimationSpec: AnimationSpec<Float> = tween(
        durationMillis = 100,
        easing = LinearEasing
    )

    override fun enter(): EnterTransition =
        slideInHorizontally(
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        ) { -(it.toFloat() * 0.3f).toInt() }

    override fun exit(): ExitTransition =
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        ) { it }
}