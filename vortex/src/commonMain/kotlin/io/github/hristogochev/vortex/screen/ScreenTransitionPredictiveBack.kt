package io.github.hristogochev.vortex.screen

import androidx.compose.animation.core.AnimationSpec

public interface ScreenTransitionPredictiveBack : ScreenTransition {
    public val cancelAnimationSpec: AnimationSpec<Float>
}