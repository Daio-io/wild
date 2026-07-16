// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import io.daio.wild.style.StyleScope

/**
 * Applies the main [Shape] to the element as part of the [StyleScopeParentNode].
 */
internal class ShapeLayoutElement(
    val shape: Shape = RectangleShape,
    val alpha: Float = 1f,
) : ModifierNodeElement<ShapeLayoutModifier>() {
    override fun create() = ShapeLayoutModifier(shape = shape, alpha = alpha)

    override fun update(node: ShapeLayoutModifier) {
        node.updateShape(shape = shape, alpha = alpha)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "ShapeLayoutElement"
        properties["shape"] = shape
        properties["alpha"] = alpha
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ShapeLayoutElement

        if (alpha != other.alpha) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = alpha.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }
}

internal class ShapeLayoutModifier(
    var shape: Shape,
    var alpha: Float,
) : LayoutModifierNode,
    Modifier.Node(),
    StyleScopeChildNode {
    /**
     * Invalidation is handled by [updateShape]
     */
    override val shouldAutoInvalidate: Boolean
        get() = false

    override fun onAttach() {
        requestInitialStyleFromParent()
    }

    override fun onReset() {
        shape = RectangleShape
        alpha = 1f
    }

    fun updateShape(
        shape: Shape,
        alpha: Float,
    ) {
        if (this.shape != shape || this.alpha != alpha) {
            val hadLayer = needsShapeLayer(this.shape, this.alpha)
            this.shape = shape
            this.alpha = alpha
            val needsLayer = needsShapeLayer(shape, alpha)
            if (hadLayer != needsLayer || needsLayer) {
                invalidatePlacement()
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val currentShape = this@ShapeLayoutModifier.shape
            val currentAlpha = this@ShapeLayoutModifier.alpha
            if (needsShapeLayer(currentShape, currentAlpha)) {
                placeable.placeWithLayer(0, 0) {
                    alpha = currentAlpha
                    shape = currentShape
                    clip = true
                    // Auto lets Compose choose the cheapest isolation required for
                    // clipping and group alpha. Offscreen is only selected when
                    // overlapping content or another operation requires it.
                    compositingStrategy = CompositingStrategy.Auto
                }
            } else {
                placeable.place(0, 0)
            }
        }
    }

    override fun updateStyle(styleScope: StyleScope) {
        if (!isAttached) return
        updateShape(
            shape = styleScope.shape,
            alpha = styleScope.alpha,
        )
    }
}

internal fun needsShapeLayer(
    shape: Shape,
    alpha: Float,
): Boolean {
    // RectangleShape is intentionally checked by identity: Shape has no
    // cross-platform value equality contract, and a custom rectangle-equivalent
    // shape may still carry different outline or clip semantics.
    return shape !== RectangleShape || alpha != 1f
}
