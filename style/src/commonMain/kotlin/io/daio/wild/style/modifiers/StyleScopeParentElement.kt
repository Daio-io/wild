// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.Style
import io.daio.wild.style.StyleScope

internal class StyleScopeParentElement(
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val resolver: StyleResolver,
) : ModifierNodeElement<StyleScopeParentNode>() {
    override fun create() =
        StyleScopeParentNode(
            resolver = resolver,
            enabled = enabled,
            selected = selected,
        )

    override fun update(node: StyleScopeParentNode) {
        node.updateState(selected = selected, enabled = enabled, resolver = resolver)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "StyleScopeParentElement"
        properties["resolver"] = resolver
        properties["enabled"] = enabled
        properties["selected"] = selected
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StyleScopeParentElement

        if (enabled != other.enabled) return false
        if (selected != other.selected) return false
        if (resolver != other.resolver) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + resolver.hashCode()
        return result
    }
}

internal class StyleScopeParentNode(
    var resolver: StyleResolver,
    enabled: Boolean = true,
    selected: Boolean = false,
) : StyleScope,
    TraversableNode,
    InteractionSourceObserverNode,
    ObserverModifierNode,
    Modifier.Node() {
    override var color: Color = Color.Unspecified
    override var alpha: Float = 1f
    override var scale: Float = 1f
    override var shape: Shape = RectangleShape
    override var border: Border = BorderDefaults.None
    override var scaleAnimationSpec: AnimationSpec<Float>? = null

    override val focused: Boolean
        get() = _focused

    override val hovered: Boolean
        get() = _hovered

    override val pressed: Boolean
        get() = _pressed

    override val selected: Boolean
        get() = _selected

    override val enabled: Boolean
        get() = _enabled

    private var _enabled: Boolean = enabled
    private var _focused: Boolean = false
    private var _hovered: Boolean = false
    private var _pressed: Boolean = false
    private var _selected: Boolean = selected
    private var lastDispatchedStyle: StyleScopeSnapshot? = null
    private var isUpdating: Boolean = false
    private var needsUpdate: Boolean = false

    fun updateState(
        selected: Boolean,
        enabled: Boolean,
        resolver: StyleResolver,
    ) {
        if (_enabled != enabled || _selected != selected || this.resolver != resolver) {
            _enabled = enabled
            _selected = selected
            this.resolver = resolver
            StyleParentTestCounters.onUpdate()
            updateStyle()
        }
    }

    override fun onAttach() {
        updateStyle()
    }

    private fun updateStyle() {
        if (isUpdating) {
            needsUpdate = true
            return
        }

        isUpdating = true
        try {
            do {
                needsUpdate = false
                resolveStyle()
                dispatchResolvedStyle()
            } while (needsUpdate)
        } finally {
            isUpdating = false
        }
    }

    private fun resolveStyle() {
        StyleParentTestCounters.onResolve()
        when (val currentResolver = resolver) {
            is StyleResolver.Block ->
                observeReads {
                    resetResolvedStyle()
                    currentResolver.block(this)
                }

            is StyleResolver.Value -> resolveValue(currentResolver.style)
        }
    }

    private fun resolveValue(style: Style) {
        color =
            style.colors.colorFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        scale =
            style.scale.scaleFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        alpha =
            style.alpha.alphaFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        shape =
            style.shapes.shapeFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        border =
            style.borders.borderFor(
                enabled = enabled,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selected,
            )
        scaleAnimationSpec = style.scale.animationSpec
    }

    private fun dispatchResolvedStyle() {
        val resolvedStyle = styleScopeSnapshot()
        if (resolvedStyle == lastDispatchedStyle) return
        lastDispatchedStyle = resolvedStyle

        traverseDirectDescendants<StyleScopeChildNode>(key = StyleChildTraversalKey) {
            it.updateStyle(this)
        }
    }

    override fun onObservedReadsChanged() {
        if (isAttached) updateStyle()
    }

    private fun resetResolvedStyle() {
        color = Color.Unspecified
        alpha = 1f
        scale = 1f
        shape = RectangleShape
        border = BorderDefaults.None
        scaleAnimationSpec = null
    }

    private fun styleScopeSnapshot() =
        StyleScopeSnapshot(
            color = color,
            alpha = alpha,
            scale = scale,
            shape = shape,
            border = border,
            scaleAnimationSpec = scaleAnimationSpec,
            focused = focused,
            hovered = hovered,
            pressed = pressed,
            selected = selected,
            enabled = enabled,
        )

    override fun onInteractionStateChanged(interactions: Interactions) {
        if (_focused != interactions.focused ||
            _pressed != interactions.pressed ||
            _hovered != interactions.hovered
        ) {
            _focused = interactions.focused
            _pressed = interactions.pressed
            _hovered = interactions.hovered
            updateStyle()
        }
    }

    override fun onReset() {
        resetResolvedStyle()
        lastDispatchedStyle = null
        isUpdating = false
        needsUpdate = false
    }

    override val traverseKey: Any = StyleParentTraversalKey
}

/** Complete [StyleScope] state observed by descendant style nodes. */
private data class StyleScopeSnapshot(
    val color: Color,
    val alpha: Float,
    val scale: Float,
    val shape: Shape,
    val border: Border,
    val scaleAnimationSpec: AnimationSpec<Float>?,
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
    val selected: Boolean,
    val enabled: Boolean,
)
