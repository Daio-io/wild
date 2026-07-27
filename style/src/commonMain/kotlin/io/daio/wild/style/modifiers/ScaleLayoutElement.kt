// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import io.daio.wild.style.StyleScope
import io.daio.wild.style.defaultScaleAnimationSpec
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Applies scale and focus z-index for the traversable style chain.
 *
 * Z-index still uses layout [placeWithLayer]. Animated scale is applied at draw time so each
 * animation frame only [invalidateDraw]s — matching the THE-217 candidate and avoiding layout
 * work on every scale tick.
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
    DrawModifierNode,
    Modifier.Node(),
    StyleScopeChildNode {
    private var scaleState = AnimationState(initialValue = scale)
    internal val animatedScale: Float
        get() = scaleState.value

    /**
     * Invalidation is handled by [updateScale] / animation frames.
     */
    override val shouldAutoInvalidate: Boolean
        get() = false

    private var updateJob: Job? = null
    internal val isScaleAnimationRunningForTest: Boolean
        get() = updateJob?.isActive == true
    private val animationRequestCoalescer = ScaleAnimationRequestCoalescer()

    override fun onAttach() {
        requestInitialStyleFromParent()
    }

    override fun onReset() {
        updateJob?.cancel()
        updateJob = null
        animationRequestCoalescer.reset()
        scaleState = AnimationState(initialValue = 1f)
        scale = 1f
        zIndex = 0f
        customAnimationSpec = null
    }

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

    private var customAnimationSpec: AnimationSpec<Float>? = null

    private fun updateScale(
        scale: Float,
        zIndex: Float,
        focused: Boolean,
        pressed: Boolean,
        hovered: Boolean,
        animationSpec: AnimationSpec<Float>? = customAnimationSpec,
    ) {
        this.scale = scale
        if (this.zIndex != zIndex) {
            this.zIndex = zIndex
            invalidatePlacement()
        }

        if (
            !animationRequestCoalescer.shouldAnimate(
                scale = scale,
                animationSpec = animationSpec,
                focused = focused,
                pressed = pressed,
                hovered = hovered,
            )
        ) {
            return
        }

        val effectiveAnimationSpec =
            animationSpec
                ?: defaultScaleAnimationSpec(
                    focused = focused,
                    pressed = pressed,
                    hovered = hovered,
                )

        updateJob?.cancel()
        updateJob =
            coroutineScope.launch {
                try {
                    scaleState.animateTo(
                        targetValue = scale,
                        animationSpec = effectiveAnimationSpec,
                    ) {
                        invalidateDraw()
                    }
                } finally {
                    invalidateDraw()
                }
            }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            if (needsZIndexLayer(zIndex)) {
                placeable.placeWithLayer(0, 0, zIndex = zIndex) {}
            } else {
                placeable.place(0, 0)
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val s = scaleState.value
        if (needsDrawScale(s)) {
            val contentDrawScope = this
            scale(s, s) {
                with(contentDrawScope) {
                    drawContent()
                }
            }
        } else {
            drawContent()
        }
    }

    override fun updateStyle(styleScope: StyleScope) {
        if (!isAttached) return
        customAnimationSpec = styleScope.scaleAnimationSpec
        updateScale(
            scale = styleScope.scale,
            zIndex = if (styleScope.focused || styleScope.hovered) 0.5f else 0f,
            focused = styleScope.focused,
            pressed = styleScope.pressed,
            hovered = styleScope.hovered,
            animationSpec = styleScope.scaleAnimationSpec,
        )
    }
}

/** True when layout must install a graphics layer solely for z-index. */
internal fun needsZIndexLayer(zIndex: Float): Boolean = zIndex != 0f

/** True when draw must apply a non-identity scale. */
internal fun needsDrawScale(animatedScale: Float): Boolean = animatedScale != 1f

/**
 * Legacy helper: true when either draw scale or z-index layer is required.
 * Prefer [needsDrawScale] / [needsZIndexLayer] for new call sites.
 */
internal fun needsScaleLayer(
    animatedScale: Float,
    zIndex: Float,
): Boolean = needsDrawScale(animatedScale) || needsZIndexLayer(zIndex)

internal enum class ScaleDefaultAnimationSpecKind {
    Pressed,
    Resting,
}

internal class ScaleAnimationRequestCoalescer {
    private var lastScale: Float? = null
    private var lastCustomAnimationSpec: AnimationSpec<Float>? = null
    private var lastDefaultAnimationSpecKind: ScaleDefaultAnimationSpecKind? = null

    fun shouldAnimate(
        scale: Float,
        animationSpec: AnimationSpec<Float>? = null,
        focused: Boolean = false,
        pressed: Boolean = false,
        hovered: Boolean = false,
    ): Boolean {
        val defaultAnimationSpecKind =
            defaultAnimationSpecKind(
                pressed = pressed,
                focused = focused,
                hovered = hovered,
            )
        val shouldAnimate =
            lastScale == null ||
                scale != lastScale ||
                hasAnimationSpecChanged(
                    animationSpec = animationSpec,
                    defaultAnimationSpecKind = defaultAnimationSpecKind,
                )

        lastScale = scale
        lastCustomAnimationSpec = animationSpec
        lastDefaultAnimationSpecKind =
            if (animationSpec == null) {
                defaultAnimationSpecKind
            } else {
                null
            }

        return shouldAnimate
    }

    fun reset() {
        lastScale = null
        lastCustomAnimationSpec = null
        lastDefaultAnimationSpecKind = null
    }

    private fun hasAnimationSpecChanged(
        animationSpec: AnimationSpec<Float>?,
        defaultAnimationSpecKind: ScaleDefaultAnimationSpecKind,
    ): Boolean =
        when {
            animationSpec != null && lastCustomAnimationSpec != null ->
                animationSpec !== lastCustomAnimationSpec && animationSpec != lastCustomAnimationSpec
            animationSpec == null && lastCustomAnimationSpec == null ->
                defaultAnimationSpecKind != lastDefaultAnimationSpecKind
            else -> true
        }

    private fun defaultAnimationSpecKind(
        pressed: Boolean,
        focused: Boolean,
        hovered: Boolean,
    ): ScaleDefaultAnimationSpecKind =
        when {
            pressed -> ScaleDefaultAnimationSpecKind.Pressed
            focused || hovered -> ScaleDefaultAnimationSpecKind.Resting
            else -> ScaleDefaultAnimationSpecKind.Resting
        }
}
