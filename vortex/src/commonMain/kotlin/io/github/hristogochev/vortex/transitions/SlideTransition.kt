package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.unit.IntOffset
import io.github.hristogochev.vortex.screen.ScreenTransition

public enum class SlideOrientation {
    Horizontal,
    Vertical
}

public enum class SlideDirection(
    public val initial: (size: Int) -> Int,
    public val target: (size: Int) -> Int,
) {
    Backward({ size: Int -> -size }, { size: Int -> size }),
    Forward({ size: Int -> size }, { size: Int -> -size })
}

private val animationSpec: FiniteAnimationSpec<IntOffset> = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
)

public sealed interface SlideTransition : ScreenTransition {

    public sealed interface Horizontal : SlideTransition {
        public data object Appear : Horizontal {

            private val offsets = SlideDirection.Forward

            override fun enter(): EnterTransition =
                slideInHorizontally(animationSpec, offsets.initial)

            override fun exit(): ExitTransition =
                slideOutHorizontally(animationSpec, offsets.target)
        }

        public data object Disappear : Horizontal {

            private val offsets = SlideDirection.Backward

            override fun enter(): EnterTransition =
                slideInHorizontally(animationSpec, offsets.initial)

            override fun exit(): ExitTransition =
                slideOutHorizontally(animationSpec, offsets.target)
        }
    }

    public sealed interface Vertical : SlideTransition {
        public data object Appear : Vertical {
            private val offsets = SlideDirection.Forward
            override fun enter(): EnterTransition =
                slideInVertically(animationSpec, offsets.initial)

            override fun exit(): ExitTransition =
                slideOutVertically(animationSpec, offsets.target)
        }

        public data object Disappear : Vertical {
            private val offsets = SlideDirection.Backward
            override fun enter(): EnterTransition =
                slideInVertically(animationSpec, offsets.initial)

            override fun exit(): ExitTransition =
                slideOutVertically(animationSpec, offsets.target)
        }
    }
}
