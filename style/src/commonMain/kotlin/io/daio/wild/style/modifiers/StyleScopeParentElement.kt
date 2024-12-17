// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.traverseDescendants
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.StyleScope
import kotlinx.coroutines.launch

internal class StyleScopeParentElement(
    val interactionSource: InteractionSource?,
    val enabled: Boolean,
    val selected: Boolean,
    val block: StyleScope.() -> Unit,
) : ModifierNodeElement<StyleScopeParentNode>() {
    override fun create() = StyleScopeParentNode(interactionSource = interactionSource, block = block)

    override fun update(node: StyleScopeParentNode) {
        node.interactionSource = interactionSource
        node.enabled = enabled
        node.selected = selected
        node.updateBlock(block)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "StyleScopeParentElement"
        properties["interactionSource"] = interactionSource
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
        if (interactionSource != other.interactionSource) return false
        if (block != other.block) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + selected.hashCode()
        result = 31 * result + (interactionSource?.hashCode() ?: 0)
        result = 31 * result + block.hashCode()
        return result
    }
}

internal class StyleScopeParentNode(
    var interactionSource: InteractionSource?,
    var block: StyleScope.() -> Unit,
) : StyleScope, TraversableNode, Modifier.Node() {
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

    private var _focused: Boolean = false
    private var _hovered: Boolean = false
    private var _pressed: Boolean = false

    override var selected: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                updateStyle(this)
            }
        }

    override var enabled: Boolean = true
        set(value) {
            if (value != field) {
                field = value
                updateStyle(this)
            }
        }

    fun updateBlock(block: StyleScope.() -> Unit) {
        this.block = block
        this.block.invoke(this)
    }

    override fun onAttach() {
        super.onAttach()
        updateStyle(this)

        coroutineScope.launch {
            interactionSource?.interactions?.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> _pressed = true
                    is PressInteraction.Release -> _pressed = false
                    is PressInteraction.Cancel -> _pressed = false
                    is HoverInteraction.Enter -> _hovered = true
                    is HoverInteraction.Exit -> _hovered = false
                    is FocusInteraction.Focus -> _focused = true
                    is FocusInteraction.Unfocus -> _focused = false
                }
                updateStyle(this@StyleScopeParentNode)
            }
        }
    }

    private fun updateStyle(styleScope: StyleScope) {
        block.invoke(styleScope)

        traverseDescendants(key = StyleTraverseKey) {
            if (it is StyleScopeChildNode && it.findNearestAncestor(StyleParentTraversalKey) === this) {
                it.updateStyle(styleScope)
                return@traverseDescendants TraversableNode.Companion.TraverseDescendantsAction.ContinueTraversal
            }

            TraversableNode.Companion.TraverseDescendantsAction.CancelTraversal
        }
    }

    override val traverseKey: Any = StyleParentTraversalKey
}
