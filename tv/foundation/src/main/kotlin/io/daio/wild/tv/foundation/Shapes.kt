package io.daio.wild.tv.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Immutable
data class Shapes(
    val shape: Shape = RectangleShape,
    val focusedShape: Shape,
    val pressedShape: Shape,
    val disabledShape: Shape,
    val focusedDisabledShape: Shape,
) {
    fun shapeFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
    ): Shape {
        return when {
            pressed && enabled -> pressedShape
            focused && enabled -> focusedShape
            !enabled && focused -> focusedDisabledShape
            !enabled -> disabledShape
            else -> shape
        }
    }
}
