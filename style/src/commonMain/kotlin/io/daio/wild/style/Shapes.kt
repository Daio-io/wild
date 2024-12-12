// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
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
    val focusedSelectedShape: Shape = focusedShape,
    val pressedSelectedShape: Shape = pressedShape,
    val hoveredSelectedShape: Shape = hoveredShape,
    val focusedDisabledShape: Shape = disabledShape,
    val pressedDisabledShape: Shape = disabledShape,
    val hoveredDisabledShape: Shape = disabledShape,
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
            pressed && selected && enabled -> pressedSelectedShape
            hovered && selected && enabled -> hoveredSelectedShape
            focused && selected && enabled -> focusedSelectedShape
            pressed && enabled -> pressedShape
            hovered && enabled -> hoveredShape
            focused && enabled -> focusedShape
            selected && enabled -> selectedShape
            !enabled && pressed -> pressedDisabledShape
            !enabled && hovered -> hoveredDisabledShape
            !enabled && focused -> focusedDisabledShape
            !enabled -> disabledShape
            else -> shape
        }
    }
}
