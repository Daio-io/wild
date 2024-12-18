// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import io.daio.wild.style.StyleScope

/**
 * Applies the background color to the element as part of the [StyleScopeParentNode].
 */
internal class BackgroundElement(
    private val color: Color = Color.Unspecified,
    private val brush: Brush? = null,
    private val alpha: Float = 1f,
    private val shape: Shape = RectangleShape,
) : ModifierNodeElement<BackgroundNode>() {
    override fun create(): BackgroundNode =
        BackgroundNode(
            brush = brush,
            alpha = alpha,
            color = color,
            shape = shape,
        )

    override fun update(node: BackgroundNode) {
        node.brush = brush
        node.alpha = alpha
        node.updateBackground(color = color, shape = shape)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "BackgroundElement"
        properties["color"] = color
        properties["alpha"] = alpha
        properties["brush"] = brush
        properties["shape"] = shape
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + (brush?.hashCode() ?: 0)
        result = 31 * result + alpha.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? BackgroundElement ?: return false
        return color == otherModifier.color &&
            brush == otherModifier.brush &&
            alpha == otherModifier.alpha &&
            shape == otherModifier.shape
    }
}

internal class BackgroundNode(
    var brush: Brush?,
    var alpha: Float,
    private var color: Color,
    private var shape: Shape,
) : DrawModifierNode, Modifier.Node(), ObserverModifierNode, StyleScopeChildNode {
    // Naively cache outline calculation if input parameters are the same, we manually observe
    // reads inside shape#createOutline separately
    private var lastSize: Size = Size.Unspecified
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null
    private var lastShape: Shape? = null

    /**
     * Invalidation is handled by [updateBackground]
     */
    override val shouldAutoInvalidate: Boolean
        get() = false

    fun updateBackground(
        color: Color,
        shape: Shape,
    ) {
        if (this.color != color || this.shape != shape) {
            this.color = color
            this.shape = shape
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        if (shape === RectangleShape) {
            // shortcut to avoid Outline calculation and allocation
            drawRect()
        } else {
            drawOutline()
        }
        drawContent()
    }

    override fun onObservedReadsChanged() {
        // Reset cached properties
        lastSize = Size.Unspecified
        lastLayoutDirection = null
        lastOutline = null
        lastShape = null
        // Invalidate draw so we build the cache again - this is needed because observeReads within
        // the draw scope obscures the state reads from the draw scope's observer
        invalidateDraw()
    }

    private fun ContentDrawScope.drawRect() {
        if (color.isSpecified) {
            drawRect(color = color)
        }
        brush?.let { drawRect(brush = it, alpha = alpha) }
    }

    private fun ContentDrawScope.drawOutline() {
        val outline = getOutline()
        if (color.isSpecified) {
            drawOutline(outline, color = color)
        }
        brush?.let { drawOutline(outline, brush = it, alpha = alpha) }
    }

    private fun ContentDrawScope.getOutline(): Outline {
        var outline: Outline? = null
        if (size == lastSize && layoutDirection == lastLayoutDirection && lastShape == shape) {
            outline = lastOutline!!
        } else {
            // Manually observe reads so we can directly invalidate the outline when it changes
            observeReads {
                outline = shape.createOutline(size, layoutDirection, this)
            }
        }
        lastOutline = outline
        lastSize = size
        lastLayoutDirection = layoutDirection
        lastShape = shape
        return outline!!
    }

    override fun updateStyle(styleScope: StyleScope) {
        updateBackground(
            color = styleScope.color,
            shape = styleScope.shape,
        )
    }
}
