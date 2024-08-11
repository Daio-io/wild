package io.daio.wild.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Colors(
    val color: Color,
    val focusedColor: Color,
    val pressedColor: Color,
    val disabledColor: Color,
    val focusedDisabledColor: Color,
) {
    fun colorFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
    ): Color {
        return when {
            pressed && enabled -> pressedColor
            focused && enabled -> focusedColor
            !enabled && focused -> focusedDisabledColor
            !enabled -> disabledColor
            else -> color
        }
    }
}
