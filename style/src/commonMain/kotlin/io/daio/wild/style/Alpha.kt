// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

internal const val DEFAULT_DISABLED_ALPHA = .6f

@Immutable
data class Alpha(
    val alpha: Float = 1f,
    val focusedAlpha: Float = alpha,
    val hoveredAlpha: Float = alpha,
    val pressedAlpha: Float = alpha,
    val selectedAlpha: Float = alpha,
    val disabledAlpha: Float = .6f,
    val focusedDisabledAlpha: Float = disabledAlpha,
    val hoveredDisabledAlpha: Float = disabledAlpha,
) {
    @Stable
    fun alphaFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Float {
        return when {
            pressed && enabled -> pressedAlpha
            hovered && enabled -> hoveredAlpha
            focused && enabled -> focusedAlpha
            selected && enabled -> selectedAlpha
            !enabled && hovered -> hoveredDisabledAlpha
            !enabled && focused -> focusedDisabledAlpha
            !enabled -> disabledAlpha
            else -> alpha
        }
    }
}
