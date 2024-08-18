package io.daio.wild.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Immutable
data class Shapes(
    val shape: Shape = RectangleShape,
    val focusedShape: Shape = shape,
    val pressedShape: Shape = focusedShape,
    val selectedShape: Shape = shape,
    val disabledShape: Shape = shape,
    val focusedDisabledShape: Shape = focusedShape,
) {
    fun shapeFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Shape {
        return when {
            pressed && enabled -> pressedShape
            focused && enabled -> focusedShape
            selected && enabled -> selectedShape
            !enabled && focused -> focusedDisabledShape
            !enabled -> disabledShape
            else -> shape
        }
    }
}
