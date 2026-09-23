// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.radio

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import io.daio.wild.components.toggleable.Selectable
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * An unstyled, caller-controlled radio button.
 *
 * The radio button does not store or toggle selection state. It invokes [onClick] for every
 * enabled click, including clicks on an already-selected item, while a disabled radio button
 * suppresses the callback. Callers enforce single selection by updating their own value and
 * passing that value to each button.
 *
 * @param selected Whether this radio button is currently selected.
 * @param onClick Callback invoked when the radio button is clicked.
 * @param modifier Modifier to apply to the radio button.
 * @param enabled Whether the radio button accepts interaction.
 * @param style The [Style] used for interaction states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param indicator Content used to render the indicator in the radio button's [BoxScope].
 *
 * @since 0.8.0
 *
 * Example:
 * ```
 * var selected by remember { mutableStateOf("small") }
 * RadioButton(
 *     selected = selected == "small",
 *     onClick = { selected = "small" },
 * ) { isSelected ->
 *     // Draw the indicator using the caller-owned selection value.
 * }
 * ```
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = RadioButtonDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    indicator: @Composable BoxScope.(selected: Boolean) -> Unit,
) {
    Selectable(
        selected = selected,
        onClick = onClick,
        modifier = modifier.semantics { role = Role.RadioButton },
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = { indicator(selected) },
    )
}

/**
 * Contains the default values used by [RadioButton].
 *
 * @since 0.8.0
 *
 * Example:
 * ```
 * RadioButton(
 *     selected = isSelected,
 *     onClick = onClick,
 *     style = RadioButtonDefaults.style(),
 * ) { isSelected ->
 *     // Draw the indicator using the caller-owned selection value.
 * }
 * ```
 */
object RadioButtonDefaults {
    /**
     * Creates an unstyled default [Style] for radio buttons.
     *
     * @param colors The colors for the radio button interaction states.
     * @param borders The borders for the radio button interaction states.
     * @param scale The scale for the radio button interaction states.
     * @param shapes The shapes for the radio button interaction states.
     * @param alpha The alpha for the radio button interaction states.
     *
     * @since 0.8.0
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
