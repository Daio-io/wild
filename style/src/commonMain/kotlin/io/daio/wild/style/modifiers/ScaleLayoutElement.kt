// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import io.daio.wild.style.StyleScope
import io.daio.wild.style.defaultScaleAnimationSpec
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Applies the scale to the element as part of the [StyleScopeParentNode].
 */
internal class ScaleLayoutElement(
    val zIndex: Float = 0f,
    val scale: Float = 1f,
) : ModifierNodeElement<ScaleLayoutModifier>() {
    override fun create() = ScaleLayoutModifier(zIndex = zIndex, scale = scale)

    override fun update(node: ScaleLayoutModifier) {
        node.updateScale(scale = scale, zIndex = zIndex)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "ScaleLayoutElement"
        properties["scale"] = scale
        properties["zIndex"] = zIndex
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ScaleLayoutElement

        if (zIndex != other.zIndex) return false
        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        var result = zIndex.hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }
}

internal class ScaleLayoutModifier(
    var zIndex: Float,
    var scale: Float,
) : LayoutModifierNode,
    Modifier.Node(),
    StyleScopeChildNode {
    private val zIndexState = AnimationState(initialValue = zIndex)
    private val scaleState = AnimationState(initialValue = scale)

    /**
     * Invalidation is handled by [updateScale]
     */
    override val shouldAutoInvalidate: Boolean
        get() = false

    fun updateScale(
        scale: Float,
        zIndex: Float,
    ) {
        if (!isAttached) {
            return
        }

        val parent =
            findNearestAncestor(StyleParentTraversalKey) as? StyleScopeParentNode ?: return

        updateScale(
            scale = scale,
            zIndex = zIndex,
            focused = parent.focused,
            pressed = parent.pressed,
            hovered = parent.hovered,
        )
    }

    private fun updateScale(
        scale: Float,
        zIndex: Float,
        focused: Boolean,
        pressed: Boolean,
        hovered: Boolean,
    ) {
        if (scaleState.value != scale || zIndexState.value != zIndex) {
            this.scale = scale
            this.zIndex = zIndex

            coroutineScope.launch {
                joinAll(
                    launch { zIndexState.animateTo(zIndex) },
                    launch {
                        scaleState.animateTo(
                            targetValue = scale,
                            animationSpec =
                                defaultScaleAnimationSpec(
                                    focused = focused,
                                    pressed = pressed,
                                    hovered = hovered,
                                ),
                        )
                    },
                )

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
            placeable.placeWithLayer(0, 0, zIndex = zIndexState.value) {
                scaleX = scaleState.value
                scaleY = scaleState.value
            }
        }
    }

    override fun updateStyle(styleScope: StyleScope) {
        updateScale(
            scale = styleScope.scale,
            zIndex = if (styleScope.focused) 0.5f else 0f,
            focused = styleScope.focused,
            pressed = styleScope.pressed,
            hovered = styleScope.hovered,
        )
    }
}
