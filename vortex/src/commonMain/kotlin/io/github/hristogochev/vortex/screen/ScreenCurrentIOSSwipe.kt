package io.github.hristogochev.vortex.screen

import androidx.compose.animation.core.Spring.StiffnessLow
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.hristogochev.vortex.annotation.ExperimentalVortexApi
import io.github.hristogochev.vortex.navigator.Navigator
import io.github.hristogochev.vortex.stack.StackEvent
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

/**
 *  Displays the current screen of a [Navigator] with an iOS-like swipe transition.
 *
 *  Accepts specifications for the draggable region of transition.
 *
 *  Ignores the override transitions for all screens rendered.
 */
@ExperimentalVortexApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun CurrentScreenIOSSwipe(
    navigator: Navigator,
    draggableHandlePadding: PaddingValues = PaddingValues(top = 80.dp),
    draggableHandleWidth: Dp = 16.dp,
    draggableHandleFillMaxHeight: Boolean = true,
    content: @Composable (Screen) -> Unit = { it.Content() },
) {

    CurrentScreenNoTransitionsDisposable(navigator)

    val density = LocalDensity.current

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxWidthPxUpdated by rememberUpdatedState(maxWidthPx)

        val anchors by remember(maxWidthPx) {
            derivedStateOf {
                DraggableAnchors {
                    DismissValue.Start at 0f
                    DismissValue.End at maxWidthPx
                }
            }
        }

        val smallThreshold = with(density) { 10.dp.toPx() }

        val offsetHistory = remember { mutableListOf<Float>() }

        val anchoredDraggableState = remember {
            AnchoredDraggableState(
                initialValue = DismissValue.Start,
                anchors = anchors,
                positionalThreshold = { distance -> distance * 0.4f }, // Threshold for snapping
                velocityThreshold = { maxWidthPxUpdated / 3 }, // Minimum velocity to
                snapAnimationSpec = SpringSpec(stiffness = StiffnessLow),
                decayAnimationSpec = exponentialDecay(),
                confirmValueChange = {
                    when (it) {
                        DismissValue.Start -> true
                        DismissValue.End -> run {
                            val last = offsetHistory.lastOrNull() ?: return@run true
                            if (offsetHistory.size < 2) {
                                return@run true
                            }
                            val secondToLast = offsetHistory[offsetHistory.size - 2]
                            offsetHistory.clear()
                            last + smallThreshold > secondToLast
                        }
                    }
                }
            )
        }

        LaunchedEffect(anchors) {
            anchoredDraggableState.updateAnchors(anchors)
        }

        val last2NavigatorItems by remember(navigator.items) {
            derivedStateOf {
                navigator.items.takeLast(2)
            }
        }

        val screens = remember {
            when (last2NavigatorItems.size) {
                1 -> mutableStateListOf(last2NavigatorItems[0])
                else -> mutableStateListOf(last2NavigatorItems[0], last2NavigatorItems[1])
            }
        }

        val lastEventUpdated by rememberUpdatedState(navigator.lastEvent)

        var autoSwipe by remember { mutableStateOf(false) }

        val offset = anchoredDraggableState.offset
        val offsetUpdated by rememberUpdatedState(offset)

        val targetState by rememberUpdatedState(anchoredDraggableState.targetValue)

        var lastShouldHaveBeen by remember { mutableStateOf<List<Screen>>(emptyList()) }

        LaunchedEffect(last2NavigatorItems) {
            when (lastEventUpdated) {
                StackEvent.Push, StackEvent.Replace -> {
                    autoSwipe = true

                    if (screens.size == 2) {
                        screens.removeAt(0)
                    }

                    anchoredDraggableState.snapTo(DismissValue.End)

                    syncScreens(last2NavigatorItems, screens)

                    snapshotFlow { autoSwipe }
                        .first { !autoSwipe }

                    anchoredDraggableState.animateTo(DismissValue.Start)
                }

                StackEvent.Pop -> {
                    if (autoSwipe) {
                        if (targetState == DismissValue.End && lastShouldHaveBeen.isNotEmpty()) {
                            anchoredDraggableState.snapTo(DismissValue.End)

                            snapshotFlow { autoSwipe }
                                .first { !autoSwipe }

                            syncScreens(lastShouldHaveBeen, screens)
                            lastShouldHaveBeen = emptyList()

                            anchoredDraggableState.snapTo(DismissValue.Start)
                        }
                    }

                    autoSwipe = true

                    lastShouldHaveBeen = last2NavigatorItems

                    anchoredDraggableState.animateTo(DismissValue.End)

                    syncScreens(last2NavigatorItems, screens)

                    snapshotFlow { autoSwipe }
                        .first { !autoSwipe }

                    anchoredDraggableState.snapTo(DismissValue.Start)
                }

                StackEvent.PopGesture -> {
                    syncScreens(last2NavigatorItems, screens)
                    anchoredDraggableState.snapTo(DismissValue.Start)
                }

                StackEvent.Idle -> {
                    syncScreens(last2NavigatorItems, screens)
                }
            }
        }

        LaunchedEffect(offset) {

            if (offset == maxWidthPxUpdated && screens.size > 1) {
                if (autoSwipe) {
                    autoSwipe = false
                } else {
                    navigator.popGesture()
                }
                return@LaunchedEffect
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            screens.forEachIndexed { index, screen ->

                val offsetModifier = when (screens.size) {
                    1 -> {
                        Modifier
                    }

                    2 -> {
                        if (index == 1) {
                            Modifier.offset { IntOffset(offset.roundToInt(), 0) }
                        } else {
                            Modifier
                                .offset {
                                    val offsetX =
                                        calculatePreviousScreenOffset(offset, maxWidthPxUpdated)
                                    IntOffset(offsetX.roundToInt(), 0)
                                }
                                .drawWithContent {
                                    // Draw the original background content.
                                    drawContent()
                                    // Calculate the transition fraction (0 when foreground fully covers, 1 when fully swiped away).
                                    val transitionFraction =
                                        (offset / maxWidthPxUpdated).coerceIn(0f, 1f)
                                    // Draw a shadow rectangle on top using your provided metric.
                                    drawRect(
                                        color = Color.Black,
                                        alpha = 0.25f - (transitionFraction * 0.25f)
                                    )
                                }

                        }
                    }

                    else -> error("Screen size must be either 1 or 2")
                }

                val draggableModifier = when (screens.size) {
                    1 -> {
                        Modifier
                    }

                    2 -> {
                        if (index == 1) {
                            Modifier.anchoredDraggable(
                                anchoredDraggableState,
                                Orientation.Horizontal
                            )
                        } else {
                            Modifier
                        }
                    }

                    else -> error("Screen size must be either 1 or 2")
                }

                val render by remember(screens.size, index, offset, maxWidthPxUpdated) {
                    derivedStateOf {
                        when (screens.size) {
                            1 -> true
                            2 -> {
                                when (index) {
                                    0 -> when (offset) {
                                        0f -> false
                                        maxWidthPxUpdated -> true
                                        else -> true
                                    }

                                    1 -> when (offset) {
                                        0f -> true
                                        maxWidthPxUpdated -> false
                                        else -> true
                                    }

                                    else -> error("Screen size must be either 1 or 2")
                                }
                            }

                            else -> error("Screen size must be either 1 or 2")
                        }
                    }
                }

                val peeking by remember(screens.size, index) {
                    derivedStateOf {
                        when (screens.size) {
                            1 -> false
                            2 -> {
                                when (index) {
                                    0 -> true
                                    1 -> false
                                    else -> error("Screen size must be either 1 or 2")
                                }
                            }

                            else -> error("Screen size must be either 1 or 2")
                        }
                    }
                }

                val moving by remember(offset, maxWidthPxUpdated) {
                    derivedStateOf {
                        offset > 0f && offset < maxWidthPxUpdated
                    }
                }

                key(screen.key) {
                    if (render) {
                        screen.render {
                            Box(
                                Modifier.fillMaxSize().zIndex(index.toFloat()).then(offsetModifier)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier.zIndex(0f).fillMaxSize()
                                    ) {
                                        content(it)
                                    }
                                    if (peeking || moving) {
                                        val interactionSource =
                                            remember { MutableInteractionSource() }
                                        Box(
                                            modifier = Modifier
                                                .zIndex(1f)
                                                .fillMaxSize()
                                                .clickable(
                                                    interactionSource = interactionSource,
                                                    indication = null,
                                                    onClick = {}
                                                ),
                                        )
                                    }
                                }
                                Box(
                                    Modifier
                                        .let {
                                            if (draggableHandleFillMaxHeight) it.fillMaxHeight() else it
                                        }
                                        .width(draggableHandleWidth)
                                        .padding(draggableHandlePadding)
                                        .then(draggableModifier)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class DismissValue {
    Start,
    End,
}

/**
 * Updates the state list of Screens (used in composition) so that its content exactly
 * matches the source list—using only minimal changes (swap or in‑place replacement)
 * to avoid restarting the composition for a screen that remains.
 *
 * • Both [source] and [state] are assumed to have either 1 or 2 items.
 * • The target order is exactly the same as the source order.
 * • If a screen with a given key is already present in [state], that instance is reused.
 *
 * For example, if initially:
 *
 *     state = [Screen1, Screen2]
 *
 * and then the new source is:
 *
 *     source = [Screen2, Screen3]
 *
 * then this function will swap the preserved Screen2 into slot 0 and update slot 1
 * with Screen3—so the final state becomes:
 *
 *     [Screen2, Screen3]
 *
 * and Screen2’s composition is not restarted.
 */
private fun syncScreens(source: List<Screen>, state: SnapshotStateList<Screen>) {
    // Operate only if both lists have 1 or 2 items.
    if (source.size !in 1..2 || state.size !in 1..2) return

    // --- CASE 1: Source has a single screen ---
    if (source.size == 1) {
        val target = source[0]
        when (state.size) {
            1 -> {
                // If the only screen is different, update it.
                if (state[0].key != target.key) {
                    state[0] = target
                }
            }

            2 -> {
                // We want exactly one screen. If one slot already has the target,
                // remove the other slot. But be careful: if the target is in slot 1,
                // remove slot 0 in a way that preserves the target instance.
                if (state[0].key == target.key && state[1].key != target.key) {
                    // The preserved screen is already at index 0.
                    state.removeAt(1)
                } else if (state[1].key == target.key && state[0].key != target.key) {
                    // The preserved screen is in index 1; swap so it moves to index 0.
                    state.swap(0, 1)
                    state.removeAt(1)
                } else {
                    // Neither slot matches; replace index 0 and remove index 1.
                    state[0] = target
                    state.removeAt(1)
                }
            }
        }
        return
    }

    // --- CASE 2: Source has 2 screens ---
    // Use the source order verbatim.
    val target0 = source[0]
    val target1 = source[1]

    if (state.size == 1) {
        // If state has one screen, preserve it if possible.
        when (state[0].key) {
            target0.key -> {
                // It already matches the first target—append the missing second.
                state.add(target1)
            }

            target1.key -> {
                // It matches the second target—insert the missing first at the beginning.
                state.add(0, target0)
            }

            else -> {
                // Otherwise, replace the only screen and then add the missing one.
                state[0] = target0
                state.add(target1)
            }
        }
        return
    }

    // At this point, state.size == 2.
    // Our goal is to have:
    //    state[0].key == target0.key
    //    state[1].key == target1.key
    //
    // If a screen with the correct key exists but is in the wrong slot,
    // swap the two slots.
    if (state[0].key != target0.key && state[1].key == target0.key) {
        // Swap so that the preserved screen (with target0) moves to index 0.
        state.swap(0, 1)
    }
    // Now, if the screen in slot 0 isn’t the target, replace it.
    if (state[0].key != target0.key) {
        state[0] = target0
    }
    // For slot 1, if the screen isn’t already target1, update it.
    if (state[1].key != target1.key) {
        state[1] = target1
    }
}

/**
 * A simple swap helper that exchanges two items in a SnapshotStateList without removing them.
 */
private fun <T> SnapshotStateList<T>.swap(i: Int, j: Int) {
    if (i == j) return
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}

/**
 * Computes the previous screen's horizontal offset as a function of the current screen's drag offset.
 *
 * @param currentOffset the current offset of the dragged (second) screen (0..screenWidth)
 * @param screenWidth the full width of the screen in pixels
 * @param maxDisplacementFraction the fraction of the screen width to displace at the start (e.g. 0.3 for 30%)
 */
private fun calculatePreviousScreenOffset(
    currentOffset: Float,
    screenWidth: Float,
    maxDisplacementFraction: Float = 0.3f,
): Float {
    // When currentOffset is 0 (no swipe), the previous screen is displaced to the left by maxDisplacement
    val maxDisplacement = screenWidth * maxDisplacementFraction
    // Calculate the interpolation fraction (0 when no swipe, 1 when fully swiped)
    val fraction = (currentOffset / screenWidth).coerceIn(0f, 1f)
    // Linear interpolation from -maxDisplacement to 0
    return -maxDisplacement + fraction * maxDisplacement
}
