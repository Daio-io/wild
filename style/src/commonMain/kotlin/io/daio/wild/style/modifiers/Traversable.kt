// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.traverseDescendants

/**
 * Executes [block] for each direct descendant of this TraversableNode with a matching [key].
 *
 * Only invokes [block] for descendants whose nearest ancestor (with this node's [traverseKey])
 * is this node, ensuring nested scopes don't leak their children into the parent's traversal.
 *
 * @param key Traversal key of the descendants.
 * @param block Callback for all matching descendants.
 * @since 0.4.0
 */
inline fun <reified T : TraversableNode> TraversableNode.traverseDirectDescendants(
    key: Any?,
    noinline block: (T) -> Unit,
) {
    traverseDescendants(key) { node ->
        if (node.node.isAttached && node is T) {
            if (node.findNearestAncestor(traverseKey) === this) {
                block(node)
            }
            // Continue traversal to find other siblings regardless of whether this node matched.
            // Nodes belonging to a nested parent are skipped by the ancestor check above.
        }
        TraversableNode.Companion.TraverseDescendantsAction.ContinueTraversal
    }
}
