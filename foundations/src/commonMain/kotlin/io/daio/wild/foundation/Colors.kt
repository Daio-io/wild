package io.daio.wild.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    val color: Color,
    val focusedColor: Color = color,
    val pressedColor: Color = focusedColor,
    val selectedColor: Color = color,
    val disabledColor: Color = color.copy(alpha = DEFAULT_DISABLED_ALPHA),
    val focusedDisabledColor: Color = disabledColor,
) {
    fun colorFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedColor
            focused && enabled -> focusedColor
            selected && enabled -> selectedColor
            !enabled && focused -> focusedDisabledColor
            !enabled -> disabledColor
            else -> color
        }
    }
}
