// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.runtime.Immutable

/**
 * Focused, hovered, and pressed flags derived from an [androidx.compose.foundation.interaction.InteractionSource].
 *
 * Does not include [io.daio.wild.foundation.InteractionState.selected] or
 * [io.daio.wild.foundation.InteractionState.enabled]; those
 * come from component parameters.
 *
 * @since 0.4.0
 */
@Immutable
data class Interactions(
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
)
