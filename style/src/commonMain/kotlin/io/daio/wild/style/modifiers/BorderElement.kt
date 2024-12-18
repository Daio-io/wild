// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.StyleScope
import io.daio.wild.style.forInnerShape

/**
 * Applies the border to the element as part of the [StyleScopeParentNode].
 */
internal class BorderElement(
    private val shape: Shape = RectangleShape,
    private val borderStroke: BorderStroke = BorderStroke(0.dp, Color.Unspecified),
    private val inset: Dp = 0.dp,
) : ModifierNodeElement<BorderNode>() {
    override fun create(): BorderNode =
        BorderNode(
            shape = shape,
            borderStroke = borderStroke,
            inset = inset,
        )

    override fun update(node: BorderNode) {
        node.updateBorder(shape = shape, borderStroke = borderStroke, inset = inset)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "BorderElement"
        properties["shape"] = shape
        properties["borderStroke"] = borderStroke
        properties["inset"] = inset
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BorderElement

        if (shape != other.shape) return false
        if (borderStroke != other.borderStroke) return false
        if (inset != other.inset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shape.hashCode()
        result = 31 * result + borderStroke.hashCode()
        result = 31 * result + inset.hashCode()
        return result
    }
}

internal class BorderNode(
    private var shape: Shape,
    private var borderStroke: BorderStroke,
    private var inset: Dp,
) : DrawModifierNode, Modifier.Node(), StyleScopeChildNode {
    private var shapeOutlineCache: ShapeOutlineCache? = null
    private var outlineStrokeCache: OutlineStrokeCache? = null

    /**
     * Invalidation is handled by [updateBorder]
     */
    override val shouldAutoInvalidate: Boolean
        get() = false

    fun updateBorder(
        shape: Shape,
        borderStroke: BorderStroke,
        inset: Dp,
    ) {
        if (this.borderStroke != borderStroke || this.inset != inset || this.shape != shape) {
            this.shape = shape
            this.borderStroke = borderStroke
            this.inset = inset

            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        drawBorder()
    }

    private fun ContentDrawScope.drawBorder() {
        if (shapeOutlineCache == null) {
            shapeOutlineCache =
                ShapeOutlineCache(
                    shape = shape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this,
                )
        }

        val borderStrokeWidthPX = with(requireDensity()) { borderStroke.width.toPx() }

        if (outlineStrokeCache == null) {
            outlineStrokeCache = OutlineStrokeCache(widthPx = borderStrokeWidthPX)
        }

        inset(inset = with(requireDensity()) { -inset.toPx() }) {
            val shapeOutline =
                shapeOutlineCache!!.updatedOutline(
                    shape = shape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = requireDensity(),
                )

            val outlineStroke =
                outlineStrokeCache!!.updatedOutlineStroke(widthPx = borderStrokeWidthPX)

            drawOutline(
                outline = shapeOutline,
                brush = borderStroke.brush,
                alpha = 1f,
                style = outlineStroke,
            )
        }
    }

    override fun updateStyle(styleScope: StyleScope) {
        updateBorder(
            shape = styleScope.border.forInnerShape(styleScope.shape),
            borderStroke = styleScope.border.borderStroke,
            inset = styleScope.border.inset,
        )
    }
}

/** Caches the outline stroke across re-compositions */
private class OutlineStrokeCache(private var widthPx: Float) {
    private var outlineStroke: Stroke? = null

    /**
     * If there are updates to the arguments, creates a new outline stroke based on the updated
     * values, else, returns the cached value
     */
    fun updatedOutlineStroke(widthPx: Float): Stroke {
        if (outlineStroke == null || this.widthPx != widthPx) {
            this.widthPx = widthPx
            createOutlineStroke()
        }
        return outlineStroke!!
    }

    private fun createOutlineStroke() {
        outlineStroke = Stroke(width = widthPx, cap = StrokeCap.Round)
    }
}

internal class ShapeOutlineCache(
    private var shape: Shape,
    private var size: Size,
    private var layoutDirection: LayoutDirection,
    private var density: Density,
) {
    private var outline: Outline? = null

    /**
     * If there are updates to the arguments, creates a new outline based on the updated values,
     * else, returns the cached value
     */
    fun updatedOutline(
        shape: Shape,
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        if (outline == null || hasUpdates(shape, size, layoutDirection, density)) {
            syncUpdates(shape, size, layoutDirection, density)
            createNewOutline()
        }
        return outline!!
    }

    private fun createNewOutline() {
        outline =
            shape.createOutline(size = size, layoutDirection = layoutDirection, density = density)
    }

    private fun syncUpdates(
        shape: Shape,
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ) {
        this.shape = shape
        this.size = size
        this.layoutDirection = layoutDirection
        this.density = density
    }

    private fun hasUpdates(
        shape: Shape,
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Boolean {
        return when {
            shape != this.shape -> true
            size != this.size -> true
            layoutDirection != this.layoutDirection -> true
            density != this.density -> true
            else -> false
        }
    }
}

fun Modifier.border(border: Border) = then(Modifier.border(border.shape, border.width, border.borderStroke, border.inset))

fun Modifier.border(
    shape: Shape = BorderDefaults.BorderDefaultShape,
    width: Dp = Dp.Unspecified,
    borderStroke: BorderStroke = BorderStroke(0.dp, Color.Unspecified),
    inset: Dp = 0.dp,
): Modifier {
    return then(
        if (width.isUnspecified) {
            Modifier
        } else {
            BorderElement(
                shape = shape,
                borderStroke = borderStroke,
                inset = inset,
            )
        },
    )
}
