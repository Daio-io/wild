// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
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
 * [BoxScope], so callers own all indicator artwork and layout.
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
 *     modifier = Modifier.size(20.dp),
 * ) { checked ->
 *     if (checked) {
 *         Box(modifier = Modifier.size(8.dp))
 *     }
 * }
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
    indicator: @Composable BoxScope.(checked: Boolean) -> Unit,
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
 * the interactive container's [BoxScope], so callers own all indicator artwork and layout.
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
 * ) { currentState ->
 *     // Render a distinct mark for ToggleableState.Indeterminate.
 * }
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
    indicator: @Composable BoxScope.(state: ToggleableState) -> Unit,
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
        colors: Colors = StyleDefaults.colors(),
        borders: Borders = StyleDefaults.borders(),
        scale: Scale = StyleDefaults.scale(),
        shapes: Shapes = StyleDefaults.shapes(),
        alpha: Alpha = StyleDefaults.alpha(),
    ): Style =
        StyleDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )
}
