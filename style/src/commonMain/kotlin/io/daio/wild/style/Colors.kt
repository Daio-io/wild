// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    /**
     * Background Colors.
     */
    val backgroundColor: Color,
    val focusedBackgroundColor: Color = backgroundColor,
    val pressedBackgroundColor: Color = focusedBackgroundColor,
    val hoveredBackgroundColor: Color = focusedBackgroundColor,
    val selectedBackgroundColor: Color = backgroundColor,
    val disabledBackgroundColor: Color = backgroundColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedSelectedBackgroundColor: Color = focusedBackgroundColor,
    val pressedSelectedBackgroundColor: Color = pressedBackgroundColor,
    val hoveredSelectedBackgroundColor: Color = hoveredBackgroundColor,
    val focusedDisabledBackgroundColor: Color = disabledBackgroundColor,
    val pressedDisabledBackgroundColor: Color = disabledBackgroundColor,
    val hoveredDisabledBackgroundColor: Color = disabledBackgroundColor,
    /**
     * Content Colors.
     */
    val contentColor: Color,
    val focusedContentColor: Color = contentColor,
    val hoveredContentColor: Color = focusedContentColor,
    val pressedContentColor: Color = focusedContentColor,
    val selectedContentColor: Color = contentColor,
    val focusedSelectedContentColor: Color = focusedContentColor,
    val pressedSelectedContentColor: Color = pressedContentColor,
    val hoveredSelectedContentColor: Color = hoveredContentColor,
    val disabledContentColor: Color = contentColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledContentColor: Color = disabledContentColor,
    val pressedDisabledContentColor: Color = disabledContentColor,
    val hoveredDisabledContentColor: Color = focusedDisabledContentColor,
) {
    @Stable
    fun colorFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedBackgroundColor
            focused && enabled -> focusedBackgroundColor
            hovered && enabled -> hoveredBackgroundColor
            selected && enabled -> selectedBackgroundColor
            selected && focused && enabled -> focusedSelectedBackgroundColor
            selected && pressed && enabled -> pressedSelectedBackgroundColor
            selected && hovered && enabled -> hoveredSelectedBackgroundColor
            !enabled && focused -> focusedDisabledBackgroundColor
            !enabled && pressed -> pressedDisabledBackgroundColor
            !enabled && hovered -> hoveredDisabledBackgroundColor
            !enabled -> disabledBackgroundColor
            else -> backgroundColor
        }
    }

    @Stable
    fun contentColorFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedContentColor
            focused && enabled -> focusedContentColor
            hovered && enabled -> hoveredContentColor
            selected && enabled -> selectedContentColor
            selected && focused && enabled -> focusedSelectedContentColor
            selected && pressed && enabled -> pressedSelectedContentColor
            selected && hovered && enabled -> hoveredSelectedContentColor
            !enabled && focused -> focusedDisabledContentColor
            !enabled && pressed -> pressedDisabledContentColor
            !enabled && hovered -> hoveredDisabledContentColor
            !enabled -> disabledContentColor
            else -> contentColor
        }
    }
}
