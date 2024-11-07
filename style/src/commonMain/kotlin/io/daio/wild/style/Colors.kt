package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    val backgroundColor: Color,
    val focusedBackgroundColor: Color = backgroundColor,
    val pressedBackgroundColor: Color = focusedBackgroundColor,
    val hoveredBackgroundColor: Color = focusedBackgroundColor,
    val selectedBackgroundColor: Color = backgroundColor,
    val disabledBackgroundColor: Color = backgroundColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledBackgroundColor: Color = disabledBackgroundColor,
    val hoveredDisabledBackgroundColor: Color = disabledBackgroundColor,
    val contentColor: Color,
    val focusedContentColor: Color = contentColor,
    val hoveredContentColor: Color = focusedContentColor,
    val pressedContentColor: Color = focusedContentColor,
    val selectedContentColor: Color = contentColor,
    val disabledContentColor: Color = contentColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledContentColor: Color = disabledContentColor,
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
            !enabled && focused -> focusedDisabledBackgroundColor
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
            !enabled && focused -> focusedDisabledContentColor
            !enabled && hovered -> hoveredDisabledContentColor
            !enabled -> disabledContentColor
            else -> contentColor
        }
    }
}
