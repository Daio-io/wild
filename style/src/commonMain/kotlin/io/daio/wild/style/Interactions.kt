// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Immutable

/**
 * Focused, hovered, and pressed flags derived from an [androidx.compose.foundation.interaction.InteractionSource].
 *
 * Does not include [io.daio.wild.foundation.InteractionState.selected] or
 * [io.daio.wild.foundation.InteractionState.enabled]; those come from component parameters.
 *
 * @since 0.4.0
 */
@Immutable
data class Interactions(
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
) {
    internal fun applyInteraction(interaction: Interaction): Interactions =
        when (interaction) {
            is PressInteraction.Press -> if (pressed) this else copy(pressed = true)
            is PressInteraction.Release,
            is PressInteraction.Cancel,
            -> if (!pressed) this else copy(pressed = false)

            is HoverInteraction.Enter -> if (hovered) this else copy(hovered = true)
            is HoverInteraction.Exit -> if (!hovered) this else copy(hovered = false)

            is FocusInteraction.Focus -> if (focused) this else copy(focused = true)
            is FocusInteraction.Unfocus -> if (!focused) this else copy(focused = false)

            else -> this
        }

    companion object {
        val None = Interactions(focused = false, hovered = false, pressed = false)
    }
}
