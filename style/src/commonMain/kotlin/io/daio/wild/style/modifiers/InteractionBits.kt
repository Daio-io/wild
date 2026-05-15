// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction

/**
 * Snapshot of interaction flags derived from an [Interaction] stream, matching [InteractionSourceNode]
 * semantics.
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

    companion object {
        val Empty = InteractionBits(focused = false, hovered = false, pressed = false)
    }
}

internal fun InteractionBits.applyInteraction(interaction: Interaction): InteractionBits =
    when (interaction) {
        is PressInteraction.Press -> copy(pressed = true)
        is PressInteraction.Release -> copy(pressed = false)
        is PressInteraction.Cancel -> copy(pressed = false)
        is HoverInteraction.Enter -> copy(hovered = true)
        is HoverInteraction.Exit -> copy(hovered = false)
        is FocusInteraction.Focus -> copy(focused = true)
        is FocusInteraction.Unfocus -> copy(focused = false)
        else -> this
    }
