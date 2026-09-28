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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

/** Read-only values exposed to a range slider track and thumb slot. */
@Stable
interface RangeSliderScope {
    val value: ClosedFloatingPointRange<Float>
    val startFraction: Float
    val endFraction: Float
    val steps: Int
    val enabled: Boolean
}

internal class RangeSliderScopeImpl(
    override val value: ClosedFloatingPointRange<Float>,
    override val startFraction: Float,
    override val endFraction: Float,
    override val steps: Int,
    override val enabled: Boolean,
) : RangeSliderScope

/** A controlled, unstyled range slider with separately addressable thumbs. */
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    startInteractionSource: MutableInteractionSource? = null,
    endInteractionSource: MutableInteractionSource? = null,
    startThumb: @Composable RangeSliderScope.() -> Unit,
    endThumb: @Composable RangeSliderScope.() -> Unit,
    track: @Composable RangeSliderScope.() -> Unit,
) {
    val state = rememberRangeSliderState(value, valueRange, steps, onValueChangeFinished)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scope =
        RangeSliderScopeImpl(
            state.value,
            if (rtl) 1f - state.startFraction else state.startFraction,
            if (rtl) 1f - state.endFraction else state.endFraction,
            steps,
            enabled,
        )
    val startSource = startInteractionSource ?: remember { MutableInteractionSource() }
    val endSource = endInteractionSource ?: remember { MutableInteractionSource() }
    var width by remember { mutableStateOf(0) }
    var focusedThumb by remember { mutableStateOf(0) }

    fun updateStart(next: Float) {
        val start = state.coerceStart(next)
        onValueChange(start..state.value.endInclusive)
    }

    fun updateEnd(next: Float) {
        val end = state.coerceEnd(next)
        onValueChange(state.value.start..end)
    }
    val chooseThumb: (Float) -> Int = { x ->
        val fraction = if (width == 0) 0f else (x / width).coerceIn(0f, 1f)
        val startDistance = kotlin.math.abs(fraction - scope.startFraction)
        val endDistance = kotlin.math.abs(fraction - scope.endFraction)
        if (startDistance < endDistance) {
            0
        } else if (endDistance < startDistance) {
            1
        } else if (fraction < scope.startFraction) {
            0
        } else {
            1
        }
    }
    val interactionModifier =
        Modifier
            .pointerInput(enabled, width) {
                detectTapGestures { offset ->
                    focusedThumb = chooseThumb(offset.x)
                    val next = fractionToValue(offset.x / width.coerceAtLeast(1), valueRange, rtl)
                    if (focusedThumb == 0) updateStart(next) else updateEnd(next)
                    onValueChangeFinished?.invoke()
                }
            }
            .pointerInput(enabled, width) {
                var drag: DragInteraction.Start? = null
                detectDragGestures(
                    onDragStart = { offset ->
                        focusedThumb = chooseThumb(offset.x)
                        val interaction = DragInteraction.Start()
                        drag = interaction
                        (if (focusedThumb == 0) startSource else endSource).tryEmit(interaction)
                    },
                    onDragCancel = {
                        drag?.let {
                            (if (focusedThumb == 0) startSource else endSource)
                                .tryEmit(DragInteraction.Cancel(it))
                        }
                        onValueChangeFinished?.invoke()
                    },
                    onDragEnd = {
                        drag?.let {
                            (if (focusedThumb == 0) startSource else endSource)
                                .tryEmit(DragInteraction.Stop(it))
                        }
                        onValueChangeFinished?.invoke()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val next = fractionToValue(change.position.x / width.coerceAtLeast(1), valueRange, rtl)
                        if (focusedThumb == 0) updateStart(next) else updateEnd(next)
                    },
                )
            }
    RangeSliderLayout(
        modifier =
            modifier
                .fillMaxWidth()
                .requiredHeightIn(min = SliderDefaults.minHeight)
                .wrapContentHeight(Alignment.CenterVertically)
                .then(if (enabled) interactionModifier else Modifier),
        onMeasured = { width = it },
        startFraction = scope.startFraction,
        endFraction = scope.endFraction,
        track = { scope.track() },
        startThumb = {
            Box(
                modifier =
                    Modifier.rangeThumbSemantics(
                        value = scope.value.start,
                        valueRange = valueRange,
                        steps = steps,
                        enabled = enabled,
                        onValueChange = ::updateStart,
                        onValueChangeFinished = onValueChangeFinished,
                    )
                        .focusable(enabled)
                        .onRangeThumbKeyEvent(
                            enabled,
                            scope.value.start,
                            valueRange,
                            steps,
                            rtl,
                            ::updateStart,
                            onValueChangeFinished,
                        ),
                content = { scope.startThumb() },
            )
        },
        endThumb = {
            Box(
                modifier =
                    Modifier.rangeThumbSemantics(
                        value = scope.value.endInclusive,
                        valueRange = valueRange,
                        steps = steps,
                        enabled = enabled,
                        onValueChange = ::updateEnd,
                        onValueChangeFinished = onValueChangeFinished,
                    )
                        .focusable(enabled)
                        .onRangeThumbKeyEvent(
                            enabled,
                            scope.value.endInclusive,
                            valueRange,
                            steps,
                            rtl,
                            ::updateEnd,
                            onValueChangeFinished,
                        ),
                content = { scope.endThumb() },
            )
        },
    )
}

private fun Modifier.onRangeThumbKeyEvent(
    enabled: Boolean,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    rtl: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
) = onKeyEvent { event ->
    if (!enabled || event.type != KeyEventType.KeyDown) return@onKeyEvent false
    val increment =
        if (steps == 0) {
            (valueRange.endInclusive - valueRange.start) / 100f
        } else {
            (valueRange.endInclusive - valueRange.start) / (steps + 1)
        }
    val current = value
    val next =
        when (event.key) {
            Key.DirectionLeft -> current + if (rtl) increment else -increment
            Key.DirectionRight -> current + if (rtl) -increment else increment
            Key.MoveHome -> valueRange.start
            Key.MoveEnd -> valueRange.endInclusive
            Key.PageUp -> current + increment * 10
            Key.PageDown -> current - increment * 10
            else -> return@onKeyEvent false
        }
    onValueChange(next)
    onValueChangeFinished?.invoke()
    true
}

private fun Modifier.rangeThumbSemantics(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
) = semantics {
    progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange, steps)
    if (!enabled) disabled()
    if (enabled) {
        setProgress {
            onValueChange(it)
            onValueChangeFinished?.invoke()
            true
        }
    }
}

@Composable
private fun RangeSliderLayout(
    modifier: Modifier,
    onMeasured: (Int) -> Unit,
    startFraction: Float,
    endFraction: Float,
    track: @Composable BoxScope.() -> Unit,
    startThumb: @Composable BoxScope.() -> Unit,
    endThumb: @Composable BoxScope.() -> Unit,
) {
    Layout(
        content = {
            Box(content = track)
            Box(content = startThumb)
            Box(content = endThumb)
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val trackPlaceable = measurables[0].measure(constraints)
        val thumbConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val startPlaceable = measurables[1].measure(thumbConstraints)
        val endPlaceable = measurables[2].measure(thumbConstraints)
        val width =
            constraints.maxWidth.coerceAtLeast(trackPlaceable.width).let {
                if (it == androidx.compose.ui.unit.Constraints.Infinity) trackPlaceable.width else it
            }
        val height = maxOf(trackPlaceable.height, startPlaceable.height, endPlaceable.height)
        onMeasured(width)
        layout(width, height) {
            trackPlaceable.place(0, (height - trackPlaceable.height) / 2)
            val startX = ((width - startPlaceable.width) * startFraction).roundToInt()
            val endX = ((width - endPlaceable.width) * endFraction).roundToInt()
            startPlaceable.place(IntOffset(startX, (height - startPlaceable.height) / 2))
            endPlaceable.place(IntOffset(endX, (height - endPlaceable.height) / 2))
        }
    }
}
