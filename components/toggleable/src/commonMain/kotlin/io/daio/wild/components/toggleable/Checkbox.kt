// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * An unstyled, caller-controlled checkbox.
 *
 * The checkbox does not store checked state. It emits the inverse of [checked] through
 * [onCheckedChange] when enabled, while a disabled checkbox suppresses the callback. The
 * [indicator] slot receives the current checked value in the interactive container's
 * [BoxScope]. By default it draws [CheckboxDefaults.Indicator]; callers can replace it
 * with custom artwork.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback receiving the next checked value when the checkbox is clicked.
 * @param modifier Modifier to apply to the checkbox.
 * @param enabled Whether the checkbox accepts interaction.
 * @param style The [Style] used for interaction states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param indicator Content used to render the indicator in the checkbox's [BoxScope].
 *
 * @since 0.7.0
 *
 * Example:
 * ```
 * Checkbox(
 *     checked = isChecked,
 *     onCheckedChange = { isChecked = it },
 * )
 * ```
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = CheckboxDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    indicator: @Composable BoxScope.(checked: Boolean) -> Unit = {
        CheckboxDefaults.Indicator(checked = it)
    },
) {
    Toggleable(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.semantics { role = Role.Checkbox },
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = { indicator(checked) },
    )
}

/**
 * An unstyled, caller-controlled tri-state checkbox.
 *
 * The checkbox does not store or cycle state. It invokes [onClick] exactly once for each
 * enabled click, leaving the caller responsible for choosing the next [ToggleableState]. A
 * disabled checkbox suppresses the callback. The [indicator] slot receives the current state in
 * the interactive container's [BoxScope]. By default it draws [CheckboxDefaults.Indicator];
 * callers can replace it with custom artwork.
 *
 * @param state The current [ToggleableState], including [ToggleableState.Indeterminate].
 * @param onClick Callback invoked once when the enabled checkbox is clicked.
 * @param modifier Modifier to apply to the checkbox.
 * @param enabled Whether the checkbox accepts interaction.
 * @param style The [Style] used for interaction states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param indicator Content used to render the indicator in the checkbox's [BoxScope].
 *
 * @since 0.7.0
 *
 * Example:
 * ```
 * TriStateCheckbox(
 *     state = state,
 *     onClick = {
 *         state = when (state) {
 *             ToggleableState.Off -> ToggleableState.On
 *             ToggleableState.On -> ToggleableState.Indeterminate
 *             ToggleableState.Indeterminate -> ToggleableState.Off
 *         }
 *     },
 * )
 * ```
 */
@Composable
fun TriStateCheckbox(
    state: ToggleableState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = CheckboxDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    indicator: @Composable BoxScope.(state: ToggleableState) -> Unit = {
        CheckboxDefaults.Indicator(state = it)
    },
) {
    Toggleable(
        state = state,
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Checkbox },
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = { indicator(state) },
    )
}

/**
 * Contains the default values used by [Checkbox] and [TriStateCheckbox].
 *
 * @since 0.7.0
 *
 * Example:
 * ```
 * val style = CheckboxDefaults.style(
 *     colors = StyleDefaults.colors(
 *         backgroundColor = Color.White,
 *         selectedBackgroundColor = Color.Green,
 *     ),
 * )
 * ```
 */
object CheckboxDefaults {
    /**
     * Default size used by [Indicator].
     *
     * @since 0.7.0
     */
    val indicatorSize: Dp = 20.dp

    /**
     * Basic Boolean checkbox indicator that draws a check mark when [checked] is true.
     *
     * Uses [LocalContentColor] by default so the mark adapts inside styled containers.
     *
     * @param checked Whether the indicator should show the checked mark.
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Stroke color for the mark. Defaults to [LocalContentColor].
     *
     * @since 0.7.0
     */
    @Composable
    fun Indicator(
        checked: Boolean,
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
    ) {
        Indicator(
            state = ToggleableState(checked),
            modifier = modifier,
            color = color,
        )
    }

    /**
     * Basic tri-state checkbox indicator.
     *
     * Draws a check for [ToggleableState.On], a dash for [ToggleableState.Indeterminate],
     * and leaves [ToggleableState.Off] empty while preserving [indicatorSize] layout.
     *
     * @param state The current [ToggleableState] to visualize.
     * @param modifier Modifier applied to the indicator canvas.
     * @param color Stroke color for the mark. Defaults to [LocalContentColor].
     *
     * @since 0.7.0
     */
    @Composable
    fun Indicator(
        state: ToggleableState,
        modifier: Modifier = Modifier,
        color: Color = LocalContentColor.current,
    ) {
        Canvas(modifier = modifier.size(indicatorSize)) {
            val stroke =
                Stroke(
                    width = size.minDimension * 0.12f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )
            when (state) {
                ToggleableState.On -> {
                    val path =
                        Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.5f)
                            lineTo(size.width * 0.42f, size.height * 0.72f)
                            lineTo(size.width * 0.8f, size.height * 0.28f)
                        }
                    drawPath(path = path, color = color, style = stroke)
                }
                ToggleableState.Indeterminate -> {
                    val y = size.height / 2f
                    drawLine(
                        color = color,
                        start = Offset(size.width * 0.2f, y),
                        end = Offset(size.width * 0.8f, y),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round,
                    )
                }
                ToggleableState.Off -> Unit
            }
        }
    }

    /**
     * Creates an unstyled default [Style] for checkboxes.
     *
     * @param colors The colors for the checkbox interaction states.
     * @param borders The borders for the checkbox interaction states.
     * @param scale The scale for the checkbox interaction states.
     * @param shapes The shapes for the checkbox interaction states.
     * @param alpha The alpha for the checkbox interaction states.
     *
     * @since 0.7.0
     */
    fun style(
        colors: Colors = StyleDefaults.None.colors,
        borders: Borders = StyleDefaults.None.borders,
        scale: Scale = StyleDefaults.None.scale,
        shapes: Shapes = StyleDefaults.None.shapes,
        alpha: Alpha = StyleDefaults.None.alpha,
    ): Style =
        StyleDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )
}
