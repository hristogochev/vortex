package io.github.hristogochev.vortex.transitions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.PathEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.hristogochev.vortex.annotation.ExperimentalVortexApi
import io.github.hristogochev.vortex.screen.ScreenTransitionPredictiveBack

/*
Urls:
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/anim/activity_open_enter.xml;bpv=1;bpt=0
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/anim/activity_open_exit.xml;bpv=1;bpt=0
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/anim/activity_close_enter.xml;bpv=1;bpt=0
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/anim/activity_close_exit.xml;bpv=1;bpt=0

Or

https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/res/res/anim/activity_open_enter.xml
https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/res/res/anim/activity_open_exit.xml
https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/res/res/anim/activity_close_enter.xml
https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/res/res/anim/activity_close_exit.xml

And then in interpolator folder you can find the interpolators
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/interpolator/fast_out_extra_slow_in.xml;bpv=1;bpt=0
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/interpolator/standard_accelerate.xml;bpv=1;bpt=0
https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/res/res/interpolator/linear.xml;bpv=1;bpt=0
 */

/* ----------  E A S I N G  ---------- */

// <pathInterpolator fast_out_extra_slow_in.xml>
private val FastOutExtraSlowInEasing = PathEasing(
    PathParser().parsePathString(
        "M0,0 C0.05,0 0.133333,0.06 0.166666,0.4 " +
                "C0.208333,0.82 0.25,1 1,1"
    ).toPath()            // parse → PathNode list → Path
)                                          // :contentReference[oaicite:0]{index=0}

// <pathInterpolator standard_accelerate.xml>
private val StandardAccelerateEasing =
    CubicBezierEasing(0.3f, 0f, 1f, 1f)  // :contentReference[oaicite:1]{index=1}

/* ----------  C O R E   S P E C S  ---------- */

private const val SLIDE_DURATION = 450       // all slide/extend anims are 450 ms
private const val PREDICTIVE_BACK_SCALE_DURATION = (SLIDE_DURATION * 0.1).toInt()
private const val FADE_DURATION = 83       // both fades last   83 ms

private val FadeOutSpec: TweenSpec<Float>
    get() = tween(
        durationMillis = FADE_DURATION,
        delayMillis = 35,                        // activity_close_exit.xml          :contentReference[oaicite:2]{index=2}
        easing = LinearEasing                    // linear.xml                       :contentReference[oaicite:3]{index=3}
    )

private val FadeInSpec: TweenSpec<Float>
    get() = tween(
        durationMillis = FADE_DURATION,
        delayMillis = 50,                        // activity_open_enter.xml          :contentReference[oaicite:4]{index=4}
        easing = LinearEasing                    // linear.xml                       :contentReference[oaicite:5]{index=5}
    )

private val SlideSpecForward: TweenSpec<IntOffset>
    get() = tween(
        durationMillis = SLIDE_DURATION,
        easing = FastOutExtraSlowInEasing        // open / close slide anims
    )

private val SlideSpecBackward: TweenSpec<IntOffset>
    get() = tween(
        durationMillis = SLIDE_DURATION,
        easing = StandardAccelerateEasing        // open_exit.xml outgoing page      :contentReference[oaicite:6]{index=6}
    )

private val SlideSpecForwardPredictiveBack: TweenSpec<IntOffset>
    get() = tween(
        durationMillis = SLIDE_DURATION,
        delayMillis = PREDICTIVE_BACK_SCALE_DURATION,
        easing = FastOutExtraSlowInEasing        // open / close slide anims
    )

/* ----------  O F F S E T   H E L P E R  ---------- */

private const val SHIFT = 96                 // the XML uses 96 dp
private fun shiftPx(density: Density) =
    with(density) { SHIFT.dp.roundToPx() }                    // density-aware px value

/* ----------  T R A N S I T I O N S  ---------- */

// Forward navigation  ➡️  (activity_open_enter / _exit)
internal fun androidPredictiveBackEnterForward(density: Density): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { shiftPx(density) },      // from +96 dp → 0                 :contentReference[oaicite:7]{index=7}
        animationSpec = SlideSpecForward
    ) + fadeIn(animationSpec = FadeInSpec)   // α 0→1

internal fun androidPredictiveBackExitForward(density: Density): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -shiftPx(density) },      // to –96 dp                       :contentReference[oaicite:8]{index=8}
        animationSpec = SlideSpecBackward
    ) + fadeOut(animationSpec = FadeOutSpec)

// Pop-back navigation  ⬅️  (activity_close_enter / _exit)
internal fun androidPredictiveBackEnterBackward(density: Density): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -shiftPx(density) },     // from –96 dp → 0                :contentReference[oaicite:9]{index=9}
        animationSpec = SlideSpecForward
    ) + fadeIn(animationSpec = FadeInSpec)

internal fun androidPredictiveBackExitBackward(density: Density): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { shiftPx(density) },       // to +96 dp                      :contentReference[oaicite:10]{index=10}
        animationSpec = SlideSpecForward
    ) + fadeOut(animationSpec = FadeOutSpec) // α 1→0

public data class AndroidSlideTransitionTransitionPredictiveBack(val density: Density) :
    ScreenTransitionPredictiveBack {

    @ExperimentalVortexApi
    override val zIndex: Float? = -1f

    @ExperimentalVortexApi
    override val cancelAnimationSpec: AnimationSpec<Float> = tween(
        durationMillis = 100,
        easing = LinearEasing
    )

    @ExperimentalVortexApi
    override fun enter(): EnterTransition = androidPredictiveBackEnterBackward(density)

    @ExperimentalVortexApi
    override fun exit(): ExitTransition = androidPredictiveBackExitBackward(density)
}