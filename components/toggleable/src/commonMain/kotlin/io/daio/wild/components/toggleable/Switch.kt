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
import io.daio.wild.style.Alpha
import io.daio.wild.style.Borders
import io.daio.wild.style.Colors
import io.daio.wild.style.Scale
import io.daio.wild.style.Shapes
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * An unstyled, caller-controlled binary switch.
 *
 * The switch does not store checked state. It emits the inverse of [checked] through
 * [onCheckedChange] when enabled, while a disabled switch suppresses the callback. The [content]
 * slot receives the current checked value in the interactive container's [BoxScope], leaving
 * track and thumb rendering to the caller. This is a binary click/tap switch; continuous thumb
 * dragging is not part of the primitive contract.
 *
 * @param checked Whether the switch is currently on.
 * @param onCheckedChange Callback receiving the next checked value when the switch is clicked.
 * @param modifier Modifier to apply to the switch.
 * @param enabled Whether the switch accepts interaction.
 * @param style The [Style] used for interaction states.
 * @param interactionSource Optional [MutableInteractionSource] for observing [Interaction]s.
 * @param content Content used to render the track and thumb in the switch's [BoxScope].
 *
 * @since 0.7.0
 *
 * Example:
 * ```
 * Switch(
 *     checked = isOn,
 *     onCheckedChange = { isOn = it },
 * ) { isChecked ->
 *     // Render a track and thumb for isChecked.
 * }
 * ```
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: Style = SwitchDefaults.style(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.(checked: Boolean) -> Unit,
) {
    Toggleable(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.semantics { role = Role.Switch },
        enabled = enabled,
        style = style,
        interactionSource = interactionSource,
        content = { content(checked) },
    )
}

/**
 * Contains the default values used by [Switch].
 *
 * @since 0.7.0
 */
object SwitchDefaults {
    /**
     * Creates the default style for a switch without imposing visual dimensions or artwork.
     *
     * @param colors The colors for the switch interaction states.
     * @param borders The borders for the switch interaction states.
     * @param scale The scale for the switch interaction states.
     * @param shapes The shapes for the switch interaction states.
     * @param alpha The alpha for the switch interaction states.
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
