// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

/** Read-only values exposed to a slider track and thumb slot. */
@Stable
interface SliderScope {
    val value: Float
    val fraction: Float
    val steps: Int
    val enabled: Boolean
}

internal class SliderScopeImpl(
    override val value: Float,
    override val fraction: Float,
    override val steps: Int,
    override val enabled: Boolean,
) : SliderScope

private fun Modifier.sliderSemantics(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
): Modifier =
    semantics {
        progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange, steps)
        if (!enabled) disabled()
        if (enabled) {
            setProgress {
                val proposed = it.coerceIn(valueRange.start, valueRange.endInclusive)
                onValueChange(proposed)
                onValueChangeFinished?.invoke()
                true
            }
        }
    }

/** A controlled, unstyled single-value slider. */
@Composable
private fun SliderImpl(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChangeFinished: (() -> Unit)?,
    interactionSource: MutableInteractionSource?,
    thumb: @Composable SliderScope.() -> Unit,
    track: @Composable SliderScope.() -> Unit,
) {
    val state = rememberSliderState(value, valueRange, steps, onValueChangeFinished)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scope = SliderScopeImpl(state.value, if (rtl) 1f - state.fraction else state.fraction, steps, enabled)
    var width by remember { mutableStateOf(0) }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val update: (Float) -> Unit = { proposed -> onValueChange(state.snap(proposed)) }
    val currentUpdate by rememberUpdatedState(update)
    val currentFinished by rememberUpdatedState(onValueChangeFinished)
    val keyboardModifier =
        Modifier.onSliderKeyEvent(enabled, state.value, valueRange, steps, rtl, update, onValueChangeFinished)
    val interactionModifier =
        Modifier
            .pointerInput(enabled, valueRange, steps, state.value) {
                detectTapGestures { offset ->
                    if (width > 0) {
                        currentUpdate(fractionToValue(offset.x / width, valueRange, rtl))
                        currentFinished?.invoke()
                    }
                }
            }
            .pointerInput(enabled, valueRange, steps) {
                var drag: DragInteraction.Start? = null
                detectDragGestures(
                    onDragStart = {
                        val interaction = DragInteraction.Start()
                        drag = interaction
                        source.tryEmit(interaction)
                    },
                    onDragCancel = {
                        drag?.let { source.tryEmit(DragInteraction.Cancel(it)) }
                        currentFinished?.invoke()
                    },
                    onDragEnd = {
                        drag?.let { source.tryEmit(DragInteraction.Stop(it)) }
                        currentFinished?.invoke()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        if (width > 0) currentUpdate(fractionToValue(change.position.x / width, valueRange, rtl))
                    },
                )
            }
    SliderLayout(
        modifier =
            modifier
                .fillMaxWidth()
                .requiredHeightIn(min = SliderDefaults.minHeight)
                .wrapContentHeight(Alignment.CenterVertically)
                .sliderSemantics(state.value, valueRange, steps, enabled, update, onValueChangeFinished)
                .focusProperties { canFocus = enabled }
                .focusable(enabled)
                .then(keyboardModifier)
                .then(if (enabled) interactionModifier else Modifier),
        onMeasured = { width = it },
        thumbFraction = scope.fraction,
        track = { scope.track() },
        thumb = { scope.thumb() },
    )
}

/**
 * A controlled, unstyled single-value slider with caller-supplied track and thumb content.
 *
 * @param value the externally controlled current value.
 * @param onValueChange called with a coerced and, when configured, snapped value.
 * @param modifier the modifier applied to the slider layout.
 * @param enabled whether the slider accepts input and can receive focus.
 * @param valueRange the inclusive range used to coerce values.
 * @param steps the number of discrete intervals between the range endpoints.
 * @param onValueChangeFinished called after a gesture or semantic update finishes.
 * @param interactionSource the optional source used for drag interactions.
 * @param thumb content for the thumb slot.
 * @param track content for the track slot.
 * @since 0.4.0
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    thumb: @Composable SliderScope.() -> Unit,
    track: @Composable SliderScope.() -> Unit,
) = SliderImpl(
    value, onValueChange, modifier, enabled, valueRange, steps, onValueChangeFinished,
    interactionSource, thumb, track,
)

@Composable
private fun SliderLayout(
    modifier: Modifier,
    onMeasured: (Int) -> Unit,
    thumbFraction: Float,
    track: @Composable BoxScope.() -> Unit,
    thumb: @Composable BoxScope.() -> Unit,
) {
    Layout(
        content = {
            Box(content = track)
            Box(content = thumb)
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val trackPlaceable = measurables[0].measure(constraints)
        val thumbPlaceable = measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val width =
            constraints.maxWidth.coerceAtLeast(trackPlaceable.width).let {
                if (it == Constraints.Infinity) trackPlaceable.width else it
            }
        val height = maxOf(trackPlaceable.height, thumbPlaceable.height)
        onMeasured(width)
        layout(width, height) {
            trackPlaceable.place(0, (height - trackPlaceable.height) / 2)
            val x = ((width - thumbPlaceable.width) * thumbFraction).roundToInt()
            thumbPlaceable.place(IntOffset(x, (height - thumbPlaceable.height) / 2))
        }
    }
}

private fun Modifier.onSliderKeyEvent(
    enabled: Boolean,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    rtl: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
): Modifier =
    onKeyEvent { event ->
        if (!enabled || event.type != KeyEventType.KeyDown) return@onKeyEvent false
        val increment =
            if (steps == 0) {
                (valueRange.endInclusive - valueRange.start) / 100f
            } else {
                (valueRange.endInclusive - valueRange.start) / (steps + 1)
            }
        val next =
            when (event.key) {
                Key.DirectionLeft -> value + if (rtl) increment else -increment
                Key.DirectionRight -> value + if (rtl) -increment else increment
                Key.MoveHome -> valueRange.start
                Key.MoveEnd -> valueRange.endInclusive
                Key.PageUp -> value + increment * 10
                Key.PageDown -> value - increment * 10
                else -> return@onKeyEvent false
            }
        onValueChange(next)
        onValueChangeFinished?.invoke()
        true
    }
