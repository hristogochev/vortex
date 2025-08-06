package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.core.AnimationSpec
import io.github.hristogochev.vortex.screen.ScreenTransition

public interface ScreenTransitionCancellable : ScreenTransition {
    public val cancellableAnimationSpec: AnimationSpec<Float>
}