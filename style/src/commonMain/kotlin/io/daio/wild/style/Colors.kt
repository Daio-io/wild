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
            selected && pressed && enabled -> pressedSelectedBackgroundColor
            selected && hovered && enabled -> hoveredSelectedBackgroundColor
            selected && focused && enabled -> focusedSelectedBackgroundColor
            pressed && enabled -> pressedBackgroundColor
            hovered && enabled -> hoveredBackgroundColor
            focused && enabled -> focusedBackgroundColor
            selected && enabled -> selectedBackgroundColor
            !enabled && pressed -> pressedDisabledBackgroundColor
            !enabled && hovered -> hoveredDisabledBackgroundColor
            !enabled && focused -> focusedDisabledBackgroundColor
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
            selected && focused && enabled -> focusedSelectedContentColor
            selected && pressed && enabled -> pressedSelectedContentColor
            selected && hovered && enabled -> hoveredSelectedContentColor
            pressed && enabled -> pressedContentColor
            focused && enabled -> focusedContentColor
            hovered && enabled -> hoveredContentColor
            selected && enabled -> selectedContentColor
            !enabled && focused -> focusedDisabledContentColor
            !enabled && pressed -> pressedDisabledContentColor
            !enabled && hovered -> hoveredDisabledContentColor
            !enabled -> disabledContentColor
            else -> contentColor
        }
    }
}

/**
 * Returns a copy of this [Colors] with the disabled alpha applied to all disabled state colors.
 *
 * This is useful for customizing the disabled appearance while preserving all other color states.
 *
 * @param alpha The alpha value to apply to disabled colors (0.0 to 1.0).
 * @return A new [Colors] instance with the specified disabled alpha.
 *
 * @since 0.5.0
 */
@Stable
fun Colors.withDisabledAlpha(alpha: Float): Colors =
    copy(
        disabledBackgroundColor = backgroundColor.copy(alpha = alpha),
        focusedDisabledBackgroundColor = backgroundColor.copy(alpha = alpha),
        pressedDisabledBackgroundColor = backgroundColor.copy(alpha = alpha),
        hoveredDisabledBackgroundColor = backgroundColor.copy(alpha = alpha),
        disabledContentColor = contentColor.copy(alpha = alpha),
        focusedDisabledContentColor = contentColor.copy(alpha = alpha),
        pressedDisabledContentColor = contentColor.copy(alpha = alpha),
        hoveredDisabledContentColor = contentColor.copy(alpha = alpha),
    )

/**
 * Returns a copy of this [Colors] with the pressed background color having an alpha multiplier
 * applied to the base background color.
 *
 * This is useful for creating a subtle pressed effect.
 *
 * @param alpha The alpha value to apply to the pressed background color (0.0 to 1.0).
 * @return A new [Colors] instance with the specified pressed alpha.
 *
 * @since 0.5.0
 */
@Stable
fun Colors.withPressedAlpha(alpha: Float): Colors =
    copy(
        pressedBackgroundColor = backgroundColor.copy(alpha = alpha),
        pressedSelectedBackgroundColor = selectedBackgroundColor.copy(alpha = alpha),
        pressedDisabledBackgroundColor = disabledBackgroundColor.copy(alpha = alpha),
    )
