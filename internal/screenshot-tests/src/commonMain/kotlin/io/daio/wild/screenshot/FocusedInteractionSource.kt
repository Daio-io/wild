// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

/**
 * Returns an [MutableInteractionSource] that emits a settled focus interaction.
 *
 * @since 0.4.0
 */
@Composable
fun rememberFocusedInteractionSource(): MutableInteractionSource {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.emit(FocusInteraction.Focus())
    }
    return interactionSource
}
