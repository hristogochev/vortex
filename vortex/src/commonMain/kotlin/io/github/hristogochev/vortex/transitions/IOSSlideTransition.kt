package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import io.github.hristogochev.vortex.screen.ScreenTransition

public sealed interface IOSSlideTransition : ScreenTransition {
    public data object Appear : IOSSlideTransition {
        override fun enter(): EnterTransition =
            slideInHorizontally(spring(stiffness = Spring.StiffnessLow)) { it }

        override fun exit(): ExitTransition =
            slideOutHorizontally(spring(stiffness = Spring.StiffnessLow)) { -(it.toFloat() * 0.3f).toInt() }
    }

    public data object Disappear : IOSSlideTransition {
        override val zIndex: Float? = -1f

        override fun enter(): EnterTransition =
            slideInHorizontally(spring(stiffness = Spring.StiffnessLow)) { -(it.toFloat() * 0.3f).toInt() }

        override fun exit(): ExitTransition =
            slideOutHorizontally(spring(stiffness = Spring.StiffnessLow)) { it }
    }
}