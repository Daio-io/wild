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

    fun updateShape(
        shape: Shape,
        alpha: Float,
    ) {
        if (this.shape != shape || this.alpha != alpha) {
            this.shape = shape
            this.alpha = alpha
            invalidatePlacement()
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                alpha = this@ShapeLayoutModifier.alpha
                shape = this@ShapeLayoutModifier.shape
                clip = true
                compositingStrategy = CompositingStrategy.Offscreen
            }
        }
    }

    override fun updateStyle(styleScope: StyleScope) {
        updateShape(
            shape = styleScope.shape,
            alpha = styleScope.alpha,
        )
    }
}
