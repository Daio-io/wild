// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Collects focused, hovered, and pressed state from [this] in a single [InteractionSource.interactions]
 * subscription, using the same rules as the style system's interaction observer (see
 * [io.daio.wild.style.modifiers.interactionSourceNode]).
 *
 * Note: `enabled` and `selected` are not part of [InteractionSource]; pass them separately when
 * resolving colors or other styled properties (for example via [Colors.contentColorFor]).
 *
 * @since 0.7.0
 */
@Composable
fun InteractionSource.collectInteractionStateAsState(): State<Interactions> {
    val state = remember { mutableStateOf(Interactions.None) }
    LaunchedEffect(this) {
        var current = Interactions.None
        interactions.collect { interaction ->
            val updated = current.applyInteraction(interaction)
            if (updated !== current) {
                current = updated
                state.value = current
            }
        }
    }
    return state
}
