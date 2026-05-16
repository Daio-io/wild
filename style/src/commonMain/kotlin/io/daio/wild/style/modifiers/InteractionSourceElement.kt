// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Interactions
import kotlinx.coroutines.Job
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
        node.updateInteractionSource(interactionSource)
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
    private var interactions: Interactions = Interactions.None

    private var collectionJob: Job? = null

    /**
     * Updates the [InteractionSource] and restarts the collection coroutine.
     * If the source hasn't changed, this is a no-op.
     */
    fun updateInteractionSource(newSource: InteractionSource?) {
        if (interactionSource === newSource) return
        interactionSource = newSource
        restartCollection()
    }

    override fun onAttach() {
        restartCollection()
    }

    private fun restartCollection() {
        collectionJob?.cancel()
        resetStateAndNotify()

        val source = interactionSource ?: return

        collectionJob =
            coroutineScope.launch {
                var collected = Interactions.None
                source.interactions.collect { interaction ->
                    val updated = collected.applyInteraction(interaction)
                    if (updated !== collected) {
                        collected = updated
                        interactions = updated
                        notifyInteractionsChanged(interactions)
                    }
                }
            }
    }

    override fun onDetach() {
        collectionJob?.cancel()
        collectionJob = null
        resetState()
    }

    override fun onReset() {
        collectionJob?.cancel()
        collectionJob = null
        resetStateAndNotify()
    }

    /**
     * Resets interaction state without notifying children.
     * Used during detach when children may already be partially detached.
     */
    private fun resetState() {
        interactions = Interactions.None
    }

    /**
     * Resets interaction state and notifies children.
     * Used during reset (node reuse) when children are still attached.
     */
    private fun resetStateAndNotify() {
        interactions = Interactions.None
        notifyInteractionsChanged(interactions)
    }

    private fun notifyInteractionsChanged(snapshot: Interactions) {
        traverseDirectDescendants<InteractionSourceObserverNode>(childTraversalKey) {
            it.onInteractionStateChanged(snapshot)
        }
    }

    private companion object InteractionSourceParentKey

    override val traverseKey: Any
        get() = InteractionSourceParentKey
}
