// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Border(
    val borderStroke: BorderStroke = BorderStroke(0.dp, Color.Unspecified),
    val inset: Dp = 0.dp,
    val shape: Shape = BorderDefaults.BorderDefaultShape,
) {
    val width: Dp
        get() = borderStroke.width
}

@Stable
fun Border(
    width: Dp = 0.dp,
    color: Color = Color.Unspecified,
    inset: Dp = 0.dp,
    shape: Shape = BorderDefaults.BorderDefaultShape,
): Border =
    Border(
        borderStroke = BorderStroke(width = width, SolidColor(color)),
        inset = inset,
        shape = shape,
    )

/**
 * Contains default values and factory functions for [Border].
 *
 * @since 0.2.0
 */
object BorderDefaults {
    /**
     * The default shape used when no shape is specified for a border.
     * This shape follows the element's inner shape.
     */
    val BorderDefaultShape = GenericShape { _, _ -> close() }

    /**
     * A border with no visual appearance.
     */
    val None = Border(width = 0.dp, color = Color.Unspecified)

    /**
     * Creates a focus ring border with an inset, commonly used for TV focus indicators.
     *
     * A focus ring is typically rendered outside the element (using [inset]) to create
     * a visible outline when the element is focused.
     *
     * @param color The color of the focus ring.
     * @param width The width of the focus ring. Defaults to 2.dp.
     * @param inset The inset (expansion) of the focus ring outside the element. Defaults to 2.dp.
     * @param shape The shape of the focus ring. Defaults to following the element's shape.
     * @return A [Border] configured as a focus ring.
     *
     * @since 0.5.0
     */
    @Stable
    fun focusRing(
        color: Color,
        width: Dp = 2.dp,
        inset: Dp = 2.dp,
        shape: Shape = BorderDefaultShape,
    ): Border =
        Border(
            width = width,
            color = color,
            inset = inset,
            shape = shape,
        )

    /**
     * Creates a simple outline border with no inset.
     *
     * @param color The color of the outline.
     * @param width The width of the outline. Defaults to 1.dp.
     * @param shape The shape of the outline. Defaults to following the element's shape.
     * @return A [Border] configured as an outline.
     *
     * @since 0.5.0
     */
    @Stable
    fun outline(
        color: Color,
        width: Dp = 1.dp,
        shape: Shape = BorderDefaultShape,
    ): Border =
        Border(
            width = width,
            color = color,
            inset = 0.dp,
            shape = shape,
        )
}

@Immutable
data class Borders(
    val border: Border = BorderDefaults.None,
    val focusedBorder: Border = border,
    val hoveredBorder: Border = focusedBorder,
    val pressedBorder: Border = focusedBorder,
    val selectedBorder: Border = border,
    val disabledBorder: Border = border,
    val focusedSelectedBorder: Border = focusedBorder,
    val pressedSelectedBorder: Border = pressedBorder,
    val hoveredSelectedBorder: Border = hoveredBorder,
    val focusedDisabledBorder: Border = disabledBorder,
    val pressedDisabledBorder: Border = disabledBorder,
    val hoveredDisabledBorder: Border = disabledBorder,
) {
    @Stable
    fun borderFor(
        enabled: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
        selected: Boolean,
    ): Border {
        return when {
            pressed && selected && enabled -> pressedSelectedBorder
            hovered && selected && enabled -> hoveredSelectedBorder
            focused && selected && enabled -> focusedSelectedBorder
            pressed && enabled -> pressedBorder
            hovered && enabled -> hoveredBorder
            focused && enabled -> focusedBorder
            selected && enabled -> selectedBorder
            !enabled && pressed -> pressedDisabledBorder
            !enabled && hovered -> hoveredDisabledBorder
            !enabled && focused -> focusedDisabledBorder
            !enabled -> disabledBorder
            else -> border
        }
    }
}

/**
 * Ensures the border shape takes into account the inner shape when applying an inset.
 */
internal fun Border.forInnerShape(innerShape: Shape): Shape {
    // If the Border shapes is using the default we want to follow the innerShape as out outline.
    val borderShape = if (shape == BorderDefaults.BorderDefaultShape) innerShape else shape
    return if (inset > 0.dp && innerShape is RoundedCornerShape && borderShape is RoundedCornerShape) {
        borderShape.toExpandedCornerShape(inset)
    } else {
        borderShape
    }
}
