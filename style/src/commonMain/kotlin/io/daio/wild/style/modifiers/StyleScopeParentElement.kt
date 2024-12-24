// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.StyleScope

internal class StyleScopeParentElement(
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val block: StyleScope.() -> Unit,
) : ModifierNodeElement<StyleScopeParentNode>() {
    override fun create() = StyleScopeParentNode(block = block)

    override fun update(node: StyleScopeParentNode) {
        node.updateState(selected = selected, enabled = enabled, block = block)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "StyleScopeParentElement"
        properties["block"] = block
        properties["enabled"] = enabled
        properties["selected"] = selected
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StyleScopeParentElement

        if (enabled != other.enabled) return false
        if (selected != other.selected) return false
        if (block != other.block) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + block.hashCode()
        return result
    }
}

internal class StyleScopeParentNode(
    var block: StyleScope.() -> Unit,
) : StyleScope,
    TraversableNode,
    InteractionSourceObserverNode,
    Modifier.Node() {
    override var color: Color = Color.Unspecified
    override var alpha: Float = 1f
    override var scale: Float = 1f
    override var shape: Shape = RectangleShape
    override var border: Border = BorderDefaults.None

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

    private var _enabled: Boolean = true
    private var _focused: Boolean = false
    private var _hovered: Boolean = false
    private var _pressed: Boolean = false
    private var _selected: Boolean = false

    fun updateState(
        selected: Boolean,
        enabled: Boolean,
        block: StyleScope.() -> Unit,
    ) {
        if (_enabled != enabled || _selected != selected || this.block != block) {
            _enabled = enabled
            _selected = selected
            this.block = block
            updateStyle()
        }
    }

    override fun onAttach() {
        updateStyle()
    }

    private fun updateStyle() {
        block.invoke(this)

        traverseDirectDescendants<StyleScopeChildNode>(key = StyleChildTraversalKey) {
            it.updateStyle(this)
        }
    }

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

    override val traverseKey: Any = StyleParentTraversalKey
}
