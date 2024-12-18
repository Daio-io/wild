// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.node.TraversableNode
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
