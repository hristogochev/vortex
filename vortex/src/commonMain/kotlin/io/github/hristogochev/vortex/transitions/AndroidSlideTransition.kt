package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import io.github.hristogochev.vortex.screen.ScreenTransition

public sealed interface AndroidSlideTransition : ScreenTransition {
    public data object Appear : AndroidSlideTransition {
        override fun enter(): EnterTransition = SlideTransition.Horizontal.Appear.enter()
        override fun exit(): ExitTransition = SlideTransition.Horizontal.Appear.exit()
    }

    public data object Disappear : AndroidSlideTransition {
        override fun enter(): EnterTransition = SlideTransition.Horizontal.Disappear.enter()
        override fun exit(): ExitTransition = SlideTransition.Horizontal.Disappear.exit()
    }
}