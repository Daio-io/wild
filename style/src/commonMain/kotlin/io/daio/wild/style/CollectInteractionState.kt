// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.daio.wild.style.modifiers.InteractionBits

/**
 * Collects focused, hovered, and pressed state from [this] in a single [InteractionSource.interactions]
 * subscription, using the same rules as the style system's interaction observer (see
 * [io.daio.wild.style.modifiers.interactionSourceNode]).
 *
 * Note: `enabled` and `selected` are not part of [InteractionSource]; pass them separately when
 * resolving colors or other styled properties (for example via [Colors.contentColorFor]).
 *
 * @since 0.6.0
 */
@Composable
fun InteractionSource.collectInteractionStateAsState(): State<Interactions> {
    val state = remember { mutableStateOf(InteractionBits.Empty.toInteractions()) }
    LaunchedEffect(this) {
        var bits = InteractionBits.Empty
        interactions.collect { interaction ->
            val updated = bits.applyInteraction(interaction)
            if (updated !== bits) {
                bits = updated
                state.value = bits.toInteractions()
            }
        }
    }
    return state
}
