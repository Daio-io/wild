// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor

/**
 * Contains the default values and indicators used by [LinearProgressIndicator] and
 * [CircularProgressIndicator].
 *
 * @since 0.7.0
 *
 * Example:
 * ```
 * LinearProgressIndicator(progress = { 0.65f }) { progress ->
 *     ProgressIndicatorDefaults.LinearIndicator(
 *         progress = progress,
 *         strokeHeight = 6.dp,
 *     )
 * }
 * ```
 */
object ProgressIndicatorDefaults {
    /**
     * Default height of a linear progress track.
     *
     * @since 0.7.0
     */
    val LinearTrackHeight: Dp = 4.dp

    /**
     * Default outer size of a circular progress indicator.
     *
     * @since 0.7.0
     */
    val CircularSize: Dp = 40.dp

    /**
     * Default stroke width of a circular progress indicator.
     *
     * @since 0.7.0
     */
    val CircularStrokeWidth: Dp = 4.dp

    /**
     * Default stroke cap for progress strokes.
     *
     * @since 0.7.0
     */
    val DefaultStrokeCap: StrokeCap = StrokeCap.Round

    /**
     * Default duration of a single indeterminate animation cycle.
     *
     * @since 0.7.0
     */
    val IndeterminateAnimationDurationMillis: Int = 1_200

    /**
     * Default fraction of the linear track covered by the indeterminate segment.
     *
     * @since 0.7.0
     */
    val LinearIndeterminateSegmentFraction: Float = 0.35f

    /**
     * Default sweep angle of the indeterminate circular arc, in degrees.
     *
     * @since 0.7.0
     */
    val CircularIndeterminateSweepDegrees: Float = 110f

    /**
     * Default alpha applied to [LocalContentColor] for the track behind the indicator.
     *
     * @since 0.7.0
     */
    val TrackAlpha: Float = 0.24f

    /**
     * Default animation spec for indeterminate progress indicators.
     *
     * @since 0.7.0
     */
    fun indeterminateAnimationSpec(durationMillis: Int = IndeterminateAnimationDurationMillis): DurationBasedAnimationSpec<Float> =
        tween(durationMillis = durationMillis, easing = LinearEasing)

    /**
     * Basic determinate linear progress indicator.
     *
     * Uses [LocalContentColor] by default so the indicator adapts inside styled containers.
     * Callers can override [strokeHeight], colors, and [strokeCap], or animate [strokeHeight]
     * by passing an animated [Dp] value.
     *
     * @param progress Normalized progress from `0f` to `1f`.
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Color of the progress segment. Defaults to [LocalContentColor].
     * @param trackColor Color of the track behind the progress segment.
     * @param strokeHeight Height of the linear track.
     * @param strokeCap Cap style applied to the progress segment ends.
     *
     * @since 0.7.0
     */
    @Composable
    fun LinearIndicator(
        progress: Float,
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
        trackColor: Color = color.copy(alpha = TrackAlpha),
        strokeHeight: Dp = LinearTrackHeight,
        strokeCap: StrokeCap = DefaultStrokeCap,
    ) {
        Canvas(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(strokeHeight),
        ) {
            val radius =
                if (strokeCap == StrokeCap.Round) {
                    size.height / 2f
                } else {
                    0f
                }
            val cornerRadius = CornerRadius(radius, radius)
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = cornerRadius,
            )
            if (progress > 0f) {
                drawRoundRect(
                    color = color,
                    size = Size(size.width * progress, size.height),
                    cornerRadius = cornerRadius,
                )
            }
        }
    }

    /**
     * Basic indeterminate linear progress indicator with a looping segment animation.
     *
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Color of the animated segment. Defaults to [LocalContentColor].
     * @param trackColor Color of the track behind the segment.
     * @param strokeHeight Height of the linear track.
     * @param strokeCap Cap style applied to the segment ends.
     * @param animationSpec Spec controlling one indeterminate cycle.
     *
     * @since 0.7.0
     */
    @Composable
    fun LinearIndeterminateIndicator(
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
        trackColor: Color = color.copy(alpha = TrackAlpha),
        strokeHeight: Dp = LinearTrackHeight,
        strokeCap: StrokeCap = DefaultStrokeCap,
        animationSpec: DurationBasedAnimationSpec<Float> = indeterminateAnimationSpec(),
    ) {
        val phase = rememberIndeterminatePhase(animationSpec)
        Canvas(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(strokeHeight),
        ) {
            val radius =
                if (strokeCap == StrokeCap.Round) {
                    size.height / 2f
                } else {
                    0f
                }
            val cornerRadius = CornerRadius(radius, radius)
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = cornerRadius,
            )
            val segmentWidth = size.width * LinearIndeterminateSegmentFraction
            val segmentStart = (size.width + segmentWidth) * phase - segmentWidth
            drawRoundRect(
                color = color,
                topLeft = Offset(segmentStart, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }

    /**
     * Basic determinate circular progress indicator.
     *
     * Uses [LocalContentColor] by default so the indicator adapts inside styled containers.
     * Callers can override [strokeWidth], colors, and [strokeCap], or animate [strokeWidth]
     * by passing an animated [Dp] value.
     *
     * @param progress Normalized progress from `0f` to `1f`.
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Color of the progress arc. Defaults to [LocalContentColor].
     * @param trackColor Color of the track behind the progress arc.
     * @param strokeWidth Width of the circular stroke.
     * @param strokeCap Cap style applied to the progress arc ends.
     *
     * @since 0.7.0
     */
    @Composable
    fun CircularIndicator(
        progress: Float,
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
        trackColor: Color = color.copy(alpha = TrackAlpha),
        strokeWidth: Dp = CircularStrokeWidth,
        strokeCap: StrokeCap = DefaultStrokeCap,
    ) {
        Canvas(modifier = modifier.size(CircularSize)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = strokeCap)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )
            if (progress > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
    }

    /**
     * Basic indeterminate circular progress indicator with a rotating arc animation.
     *
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Color of the animated arc. Defaults to [LocalContentColor].
     * @param trackColor Color of the track behind the arc.
     * @param strokeWidth Width of the circular stroke.
     * @param strokeCap Cap style applied to the arc ends.
     * @param animationSpec Spec controlling one indeterminate cycle.
     *
     * @since 0.7.0
     */
    @Composable
    fun CircularIndeterminateIndicator(
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
        trackColor: Color = color.copy(alpha = TrackAlpha),
        strokeWidth: Dp = CircularStrokeWidth,
        strokeCap: StrokeCap = DefaultStrokeCap,
        animationSpec: DurationBasedAnimationSpec<Float> = indeterminateAnimationSpec(),
    ) {
        val phase = rememberIndeterminatePhase(animationSpec)
        Canvas(modifier = modifier.size(CircularSize)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = strokeCap)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )
            drawArc(
                color = color,
                startAngle = phase * 360f,
                sweepAngle = CircularIndeterminateSweepDegrees,
                useCenter = false,
                style = stroke,
            )
        }
    }
}

@Composable
private fun rememberIndeterminatePhase(animationSpec: DurationBasedAnimationSpec<Float>): Float {
    val transition = rememberInfiniteTransition(label = "progress-indicator")
    val phase by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = animationSpec,
                    repeatMode = RepeatMode.Restart,
                ),
            label = "progress-phase",
        )
    return phase
}
