// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import io.daio.wild.style.StyleScope

/**
 * Default traversal key for [StyleScopeChildNode] traversable nodes.
 */
internal object StyleChildTraversalKey

/**
 * Default traversal key for [StyleScopeParentNode] traversable nodes.
 */
internal object StyleParentTraversalKey

/**
 * Interface to mark a Node as a child in order to apply styles in response to [StyleScope] changes
 * from [StyleScopeParentNode].
 *
 * Implementations that extend [Modifier.Node] should call [requestInitialStyleFromParent] in their
 * [onAttach] to handle the case where the child attaches after the parent has already dispatched
 * its initial style update.
 */
internal interface StyleScopeChildNode : TraversableNode {
    /**
     * Called when styles have been updated.
     *
     * @param styleScope The scope of the changes including states.
     */
    fun updateStyle(styleScope: StyleScope)

    /**
     * Traverse key used by [StyleScopeParentNode] to locate this node as a child.
     */
    override val traverseKey: Any
        get() = StyleChildTraversalKey
}

/**
 * Requests the initial style from the nearest [StyleScopeParentNode] ancestor.
 * Call this from a child node's [Modifier.Node.onAttach] to handle the case where
 * the child attaches after the parent has already dispatched its initial style.
 */
internal fun <T> T.requestInitialStyleFromParent() where T : Modifier.Node, T : StyleScopeChildNode {
    val parent =
        findNearestAncestor(StyleParentTraversalKey) as? StyleScopeParentNode ?: return
    updateStyle(parent)
}
