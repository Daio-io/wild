package io.daio.wild.foundation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Immutable
data class Border(
    val width: Dp = 0.dp,
    val color: Color = Color.Transparent,
    val inset: Dp = 0.dp,
    val shape: Shape = RectangleShape,
) {
    val borderStroke = BorderStroke(width = width, color = color)
}

object BorderDefaults {
    val BorderDefaultShape = GenericShape { _, _ -> close() }
    val None = Border(width = 0.dp, color = Color.Transparent)
}

@Immutable
data class Borders(
    val border: Border = BorderDefaults.None,
    val focusedBorder: Border = border,
    val pressedBorder: Border = border,
    val disabledBorder: Border = border,
    val focusedDisabledBorder: Border = disabledBorder,
) {
    fun borderFor(
        enabled: Boolean,
        focused: Boolean,
        pressed: Boolean,
    ): Border {
        return when {
            pressed && enabled -> pressedBorder
            focused && enabled -> focusedBorder
            !enabled && focused -> focusedDisabledBorder
            !enabled -> disabledBorder
            else -> border
        }
    }
}

fun Modifier.wildBorder(
    border: Border,
    shape: Shape = border.shape,
): Modifier {
    return then(
        if (border == BorderDefaults.None) {
            Modifier
        } else {
            BorderElement(
                shape = shape,
                border = border,
                inspectorInfo =
                    debugInspectorInfo {
                        name = "wildBorder"
                        properties["shape"] = shape
                        properties["border"] = border
                    },
            )
        },
    )
}

private class BorderElement(
    private val shape: Shape,
    private val border: Border,
    private val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<BorderNode>() {
    override fun create(): BorderNode {
        return BorderNode(
            shape = shape,
            border = border,
        )
    }

    override fun update(node: BorderNode) {
        node.reactToUpdates(
            newShape = shape,
            newBorder = border,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun hashCode(): Int {
        var result = shape.hashCode()
        result = 31 * result + border.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherTyped = other as? BorderElement ?: return false
        return shape == otherTyped.shape && border == otherTyped.border
    }
}

private class BorderNode(
    private var shape: Shape,
    private var border: Border,
) : DrawModifierNode, Modifier.Node() {
    private var shapeOutlineCache: ShapeOutlineCache? = null
    private var outlineStrokeCache: OutlineStrokeCache? = null

    fun reactToUpdates(
        newShape: Shape,
        newBorder: Border,
    ) {
        shape = newShape
        border = newBorder
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val borderStroke = border.borderStroke

        val borderShape =
            if (border.shape == BorderDefaults.BorderDefaultShape) shape else border.shape

        if (shapeOutlineCache == null) {
            shapeOutlineCache =
                ShapeOutlineCache(
                    shape = borderShape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this,
                )
        }

        if (outlineStrokeCache == null) {
            outlineStrokeCache = OutlineStrokeCache(widthPx = borderStroke.width.toPx())
        }

        inset(inset = -border.inset.toPx()) {
            val shapeOutline =
                shapeOutlineCache!!.updatedOutline(
                    shape = borderShape,
                    size = size,
                    layoutDirection = layoutDirection,
                    density = this,
                )

            val outlineStroke =
                outlineStrokeCache!!.updatedOutlineStroke(widthPx = borderStroke.width.toPx())

            drawOutline(
                outline = shapeOutline,
                brush = borderStroke.brush,
                alpha = 1f,
                style = outlineStroke,
            )
        }
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
        if (shape != this.shape) return true
        if (size != this.size) return true
        if (layoutDirection != this.layoutDirection) return true
        if (density != this.density) return true
        return false
    }
}
