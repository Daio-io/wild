package io.daio.wild.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

@Immutable
data class Shapes(
    val shape: Shape = RectangleShape,
    val focusedShape: Shape = shape,
    val hoveredShape: Shape = focusedShape,
    val pressedShape: Shape = focusedShape,
    val selectedShape: Shape = shape,
    val disabledShape: Shape = shape,
    val focusedDisabledShape: Shape = focusedShape,
    val hoveredDisabledShape: Shape = hoveredShape,
) {
    @Stable
    fun shapeFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Shape {
        return when {
            pressed && enabled -> pressedShape
            focused && enabled -> focusedShape
            hovered && enabled -> hoveredShape
            selected && enabled -> selectedShape
            !enabled && focused -> focusedDisabledShape
            !enabled && hovered -> hoveredDisabledShape
            !enabled -> disabledShape
            else -> shape
        }
    }
}
