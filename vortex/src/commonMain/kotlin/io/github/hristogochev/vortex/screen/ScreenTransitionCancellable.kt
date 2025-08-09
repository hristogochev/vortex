package io.github.hristogochev.vortex.screen

import androidx.compose.animation.core.AnimationSpec

public interface ScreenTransitionCancellable : ScreenTransition {
    public val cancellableAnimationSpec: AnimationSpec<Float>
}