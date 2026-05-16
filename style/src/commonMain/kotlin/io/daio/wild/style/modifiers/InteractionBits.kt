// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import io.daio.wild.style.Interactions

/**
 * Mutable snapshot of interaction flags derived from an [Interaction] stream, matching
 * [InteractionSourceNode] semantics.
 */
internal data class InteractionBits(
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
) {
    fun toInteractions(): Interactions =
        Interactions(
            focused = focused,
            hovered = hovered,
            pressed = pressed,
        )

    fun applyInteraction(interaction: Interaction): InteractionBits =
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
        val Empty = InteractionBits(focused = false, hovered = false, pressed = false)
    }
}
