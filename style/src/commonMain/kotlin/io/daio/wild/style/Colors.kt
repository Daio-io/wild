package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    val backgroundColor: Color,
    val focusedBackgroundColor: Color = backgroundColor,
    val pressedBackgroundColor: Color = focusedBackgroundColor,
    val selectedBackgroundColor: Color = backgroundColor,
    val disabledBackgroundColor: Color = backgroundColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledBackgroundColor: Color = disabledBackgroundColor,
    val contentColor: Color,
    val focusedContentColor: Color = contentColor,
    val pressedContentColor: Color = focusedContentColor,
    val selectedContentColor: Color = contentColor,
    val disabledContentColor: Color = contentColor.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledContentColor: Color = disabledContentColor,
) {
    @Stable
    fun colorFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedBackgroundColor
            focused && enabled -> focusedBackgroundColor
            selected && enabled -> selectedBackgroundColor
            !enabled && focused -> focusedDisabledBackgroundColor
            !enabled -> disabledBackgroundColor
            else -> backgroundColor
        }
    }

    @Stable
    fun contentColorFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedContentColor
            focused && enabled -> focusedContentColor
            selected && enabled -> selectedContentColor
            !enabled && focused -> focusedDisabledContentColor
            !enabled -> disabledContentColor
            else -> contentColor
        }
    }
}
