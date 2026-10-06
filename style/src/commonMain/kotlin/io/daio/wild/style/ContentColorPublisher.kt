// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.platform.InspectorInfo
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.modifiers.StyleParentTraversalKey
import io.daio.wild.style.modifiers.StyleScopeParentNode

/**
 * Receives equality-gated content color publications from the style parent node.
 *
 * Prefer component [StyleSpec] overloads (Container / Button / …) which own the bridge. Use this
 * interface only when wiring a custom sink after [interactionStyle]:
 *
 * Example:
 * ```
 * val sink =
 *     object : ContentColorPublisher {
 *         override fun publishResolvedColor(color: Color) {
 *             // update a remembered MutableState / binding when color changes
 *         }
 *     }
 * Modifier
 *     .interactionStyle(interactionSource = source, style = style)
 *     .contentColorBridge(sink)
 * ```
 *
 * @since 0.8.0
 */
@ExperimentalWildApi
fun interface ContentColorPublisher {
    /**
     * Publishes a newly resolved content color. Implementations should no-op when [color] equals
     * the currently published value.
     *
     * @param color Resolved content color for the current interaction and component flags.
     */
    fun publishResolvedColor(color: Color)
}

/**
 * Traversable sink used by [contentColorBridge] so the style parent can publish resolved content
 * color without composition reading interaction collectors.
 */
@ExperimentalWildApi
internal interface ContentColorBridgeNode : TraversableNode {
    fun onResolvedContentColor(color: Color)

    override val traverseKey: Any
        get() = ContentColorBridgeTraversalKey
}

@ExperimentalWildApi
internal object ContentColorBridgeTraversalKey

/**
 * Installs a content-color bridge descendant of the style parent so resolved content color is
 * published to [publisher] when it changes.
 *
 * Place after [interactionStyle] / interactable style chains. Standalone chrome modifiers that omit
 * this call do not publish content composition locals.
 *
 * Example:
 * ```
 * Modifier
 *     .interactionStyle(interactionSource = source, style = style)
 *     .contentColorBridge(publisher)
 * ```
 *
 * @param publisher Sink that receives resolved content colors.
 * @since 0.8.0
 */
@ExperimentalWildApi
fun Modifier.contentColorBridge(publisher: ContentColorPublisher): Modifier = this then ContentColorBridgeElement(publisher)

@ExperimentalWildApi
private data class ContentColorBridgeElement(
    val publisher: ContentColorPublisher,
) : ModifierNodeElement<ContentColorBridgeModifierNode>() {
    override fun create() = ContentColorBridgeModifierNode(publisher)

    override fun update(node: ContentColorBridgeModifierNode) {
        val publisherChanged = node.publisher !== publisher
        node.publisher = publisher
        // Style parent may be reused when only the sink changes; request a publication so the
        // new publisher is initialized without waiting for a later interaction/style change.
        if (publisherChanged) {
            node.requestRepublish()
        }
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "contentColorBridge"
    }
}

@ExperimentalWildApi
private class ContentColorBridgeModifierNode(
    var publisher: ContentColorPublisher,
) : Modifier.Node(),
    ContentColorBridgeNode {
    override fun onResolvedContentColor(color: Color) {
        publisher.publishResolvedColor(color)
    }

    override fun onAttach() {
        requestRepublish()
    }

    fun requestRepublish() {
        if (!isAttached) return
        val parent =
            findNearestAncestor(StyleParentTraversalKey) as? StyleScopeParentNode ?: return
        parent.republishContentColor()
    }
}
