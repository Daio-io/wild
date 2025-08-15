// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.foundation.ExperimentalWildApi
import kotlinx.coroutines.launch

/**
 * Modifier to attach an [InteractionSource] to an element allowing child traversable Nodes to
 * listen to interactions by implementing [InteractionSourceObserverNode].
 *
 * A child element implementing [InteractionSourceObserverNode] should override
 * onInteractionsChanged to respond to interaction source changes.
 *
 * @param interactionSource The [InteractionSource] to associate with a Node.
 * @param childTraversalKey The traversal key used to find the [InteractionSourceObserverNode].
 *
 * @since 0.4.0
 */
@ExperimentalWildApi
fun Modifier.interactionSourceNode(
    interactionSource: InteractionSource?,
    childTraversalKey: Any = DefaultInteractionSourceChildTraversalKey,
): Modifier = this then InteractionSourceElement(interactionSource, childTraversalKey)

object DefaultInteractionSourceChildTraversalKey

@Immutable
data class Interactions(
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
)

/**
 * Observe [InteractionSource] updated for a node within a direct child of
 * [InteractionSourceElement] nodes.
 */
interface InteractionSourceObserverNode : TraversableNode {
    /**
     * Called when any interaction such as focused, pressed or hovered changes state on the parent
     * node.
     */
    fun onInteractionStateChanged(interactions: Interactions)
}

internal class InteractionSourceElement(
    val interactionSource: InteractionSource?,
    val childTraversalKey: Any = DefaultInteractionSourceChildTraversalKey,
) : ModifierNodeElement<InteractionSourceNode>() {
    override fun create() =
        InteractionSourceNode(
            interactionSource = interactionSource,
            childTraversalKey = childTraversalKey,
        )

    override fun update(node: InteractionSourceNode) {
        node.interactionSource = interactionSource
        node.childTraversalKey = childTraversalKey
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "InteractionSourceElement"
        properties["interactionSource"] = interactionSource
        properties["childTraversalKey"] = childTraversalKey
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as InteractionSourceElement

        if (interactionSource != other.interactionSource) return false
        if (childTraversalKey != other.childTraversalKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionSource.hashCode()
        result = 31 * result + childTraversalKey.hashCode()
        return result
    }
}

internal class InteractionSourceNode(
    var interactionSource: InteractionSource?,
    var childTraversalKey: Any,
) : Modifier.Node(), TraversableNode {
    var focused: Boolean = false
        internal set

    var hovered: Boolean = false
        internal set

    var pressed: Boolean = false
        internal set

    var enabled: Boolean = false
        internal set

    var selected: Boolean = false
        internal set

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource?.interactions?.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressed = true
                    is PressInteraction.Release -> pressed = false
                    is PressInteraction.Cancel -> pressed = false
                    is HoverInteraction.Enter -> hovered = true
                    is HoverInteraction.Exit -> hovered = false
                    is FocusInteraction.Focus -> focused = true
                    is FocusInteraction.Unfocus -> focused = false
                }

                notifyInteractionsChanged(
                    Interactions(
                        focused = focused,
                        hovered = hovered,
                        pressed = pressed,
                    ),
                )
            }
        }
    }

    override fun onDetach() {
        reset()
    }

    private fun reset() {
        hovered = false
        focused = false
        pressed = false
        notifyInteractionsChanged(
            Interactions(
                focused = focused,
                hovered = hovered,
                pressed = pressed,
            ),
        )
    }

    private fun notifyInteractionsChanged(interactions: Interactions) {
        traverseDirectDescendants<InteractionSourceObserverNode>(childTraversalKey) {
            it.onInteractionStateChanged(interactions)
        }
    }

    private companion object InteractionSourceParentKey

    override val traverseKey: Any
        get() = InteractionSourceParentKey
}
