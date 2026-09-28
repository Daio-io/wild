// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private const val RANGE_ERROR = "valueRange must be finite, ascending, and non-empty"

internal fun validateRange(valueRange: ClosedFloatingPointRange<Float>) {
    require(valueRange.start.isFinite() && valueRange.endInclusive.isFinite()) {
        RANGE_ERROR
    }
    require(valueRange.start < valueRange.endInclusive) { RANGE_ERROR }
}

internal fun validateSteps(steps: Int) {
    require(steps >= 0) { "steps must be greater than or equal to 0" }
}

internal fun coerceValue(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
): Float = if (value.isNaN()) valueRange.start else value.coerceIn(valueRange.start, valueRange.endInclusive)

internal fun valueToFraction(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    rtl: Boolean = false,
): Float {
    val fraction =
        (
            (coerceValue(value, valueRange) - valueRange.start) /
                (valueRange.endInclusive - valueRange.start)
        )
    return if (rtl) 1f - fraction else fraction
}

internal fun fractionToValue(
    fraction: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    rtl: Boolean = false,
): Float {
    val normalized = (if (rtl) 1f - fraction else fraction).coerceIn(0f, 1f)
    return valueRange.start + normalized * (valueRange.endInclusive - valueRange.start)
}

internal fun snapValue(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
): Float {
    val coerced = coerceValue(value, valueRange)
    if (steps == 0) return coerced
    val count = steps + 1
    val index =
        (((coerced - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * count)
            .roundToInt()
            .coerceIn(0, count)
    return valueRange.start + (valueRange.endInclusive - valueRange.start) * index / count
}

private fun Float.roundToInt(): Int = kotlin.math.round(this).toInt()

/** State and normalization policy for a controlled [Slider]. */
@Stable
class SliderState(
    value: Float,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0,
    val onValueChangeFinished: (() -> Unit)? = null,
) {
    init {
        validateRange(valueRange)
        validateSteps(steps)
    }

    var value: Float by mutableStateOf(coerceValue(value, valueRange))
        internal set

    /** Current value as a fraction of [valueRange]. */
    val fraction: Float
        get() = valueToFraction(value, valueRange)

    internal fun synchronize(value: Float) {
        this.value = coerceValue(value, valueRange)
    }

    internal fun propose(value: Float): Float = snapValue(value, valueRange, steps)

    /** Returns [value] coerced and snapped according to this state. */
    fun snap(value: Float): Float = propose(value)
}

/** State and normalization policy for a controlled [RangeSlider]. */
@Stable
class RangeSliderState(
    value: ClosedFloatingPointRange<Float>,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0,
    val onValueChangeFinished: (() -> Unit)? = null,
) {
    init {
        validateRange(valueRange)
        validateSteps(steps)
    }

    var value: ClosedFloatingPointRange<Float> by mutableStateOf(coerceRange(value))
        internal set

    val startFraction: Float
        get() = valueToFraction(value.start, valueRange)

    val endFraction: Float
        get() = valueToFraction(value.endInclusive, valueRange)

    internal fun synchronize(value: ClosedFloatingPointRange<Float>) {
        this.value = coerceRange(value)
    }

    internal fun coerceStart(value: Float): Float = snapValue(value, valueRange, steps).coerceAtMost(this.value.endInclusive)

    internal fun coerceEnd(value: Float): Float = snapValue(value, valueRange, steps).coerceAtLeast(this.value.start)

    private fun coerceRange(value: ClosedFloatingPointRange<Float>): ClosedFloatingPointRange<Float> {
        val start = snapValue(value.start, valueRange, steps)
        val end = snapValue(value.endInclusive, valueRange, steps)
        val clampedStart = start.coerceIn(valueRange.start, valueRange.endInclusive)
        val clampedEnd = end.coerceIn(valueRange.start, valueRange.endInclusive)
        return clampedStart.coerceAtMost(clampedEnd)..clampedEnd
    }
}

/** Remembers a controlled slider state. */
@Composable
fun rememberSliderState(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
): SliderState =
    remember(valueRange, steps, onValueChangeFinished) {
        SliderState(value, valueRange, steps, onValueChangeFinished)
    }.also { it.synchronize(value) }

/** Remembers a controlled range slider state. */
@Composable
fun rememberRangeSliderState(
    value: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
): RangeSliderState =
    remember(valueRange, steps, onValueChangeFinished) {
        RangeSliderState(value, valueRange, steps, onValueChangeFinished)
    }.also { it.synchronize(value) }
