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
        node.update(
            interactionSource = interactionSource,
            childTraversalKey = childTraversalKey,
        )
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

        if (interactionSource !== other.interactionSource) return false
        if (childTraversalKey != other.childTraversalKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = System.identityHashCode(interactionSource)
        result = 31 * result + childTraversalKey.hashCode()
        return result
    }
}

internal class InteractionSourceNode(
    var interactionSource: InteractionSource?,
    var childTraversalKey: Any,
) : Modifier.Node(), TraversableNode {
    private var collectionJob: Job? = null
    private val activePresses = mutableSetOf<PressInteraction.Press>()
    private val activeHovers = mutableSetOf<HoverInteraction.Enter>()
    private val activeFocuses = mutableSetOf<FocusInteraction.Focus>()
    private var lastDispatchedStateBits: Int = STATE_NONE

    /**
     * Updates the [InteractionSource] and child traversal key atomically.
     * If only the key changes, the current state is dispatched once to the new subtree.
     */
    fun update(
        interactionSource: InteractionSource?,
        childTraversalKey: Any,
    ) {
        val sourceChanged = this.interactionSource !== interactionSource
        val keyChanged = this.childTraversalKey != childTraversalKey

        if (!sourceChanged && !keyChanged) return

        this.childTraversalKey = childTraversalKey

        if (sourceChanged) {
            this.interactionSource = interactionSource
            restartCollection()
        } else if (keyChanged) {
            dispatchCurrentState()
        }
    }

    override fun onAttach() {
        restartCollection()
    }

    private fun restartCollection() {
        collectionJob?.cancel()
        collectionJob = null
        resetState(notify = isAttached)

        val source = interactionSource ?: return

        collectionJob =
            coroutineScope.launch {
                source.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> activePresses += interaction
                        is PressInteraction.Release -> activePresses -= interaction.press
                        is PressInteraction.Cancel -> activePresses -= interaction.press
                        is HoverInteraction.Enter -> activeHovers += interaction
                        is HoverInteraction.Exit -> activeHovers -= interaction.enter
                        is FocusInteraction.Focus -> activeFocuses += interaction
                        is FocusInteraction.Unfocus -> activeFocuses -= interaction.focus
                    }

                    dispatchIfChanged()
                }
            }
    }

    override fun onDetach() {
        collectionJob?.cancel()
        collectionJob = null
        resetState(notify = false)
    }

    override fun onReset() {
        collectionJob?.cancel()
        collectionJob = null
        resetState(notify = isAttached)
    }

    private fun resetState(notify: Boolean) {
        activePresses.clear()
        activeHovers.clear()
        activeFocuses.clear()

        if (notify && lastDispatchedStateBits != STATE_NONE) {
            lastDispatchedStateBits = STATE_NONE
            notifyInteractionsChanged(interactionsFor(STATE_NONE))
        } else {
            lastDispatchedStateBits = STATE_NONE
        }
    }

    private fun dispatchCurrentState() {
        notifyInteractionsChanged(interactionsFor(currentStateBits()))
    }

    private fun dispatchIfChanged() {
        val newStateBits = currentStateBits()
        if (newStateBits == lastDispatchedStateBits) return
        lastDispatchedStateBits = newStateBits
        notifyInteractionsChanged(interactionsFor(newStateBits))
    }

    private fun currentStateBits(): Int {
        var bits = STATE_NONE
        if (activePresses.isNotEmpty()) bits = bits or STATE_PRESSED
        if (activeHovers.isNotEmpty()) bits = bits or STATE_HOVERED
        if (activeFocuses.isNotEmpty()) bits = bits or STATE_FOCUSED
        return bits
    }

    private fun notifyInteractionsChanged(interactions: Interactions) {
        traverseDirectDescendants<InteractionSourceObserverNode>(childTraversalKey) {
            it.onInteractionStateChanged(interactions)
        }
    }

    private companion object {
        object InteractionSourceParentKey

        const val STATE_NONE = 0
        const val STATE_PRESSED = 1 shl 0
        const val STATE_HOVERED = 1 shl 1
        const val STATE_FOCUSED = 1 shl 2

        val interactionStates =
            Array(8) { bits ->
                Interactions(
                    focused = bits and STATE_FOCUSED != 0,
                    hovered = bits and STATE_HOVERED != 0,
                    pressed = bits and STATE_PRESSED != 0,
                )
            }

        fun interactionsFor(bits: Int): Interactions = interactionStates[bits]
    }

    override val traverseKey: Any
        get() = InteractionSourceParentKey
}
