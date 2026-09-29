// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

/** A named collection of A2UI component implementations.
 *
 * @param catalogId protocol catalog identifier.
 * @param components components available for rendering.
 * @since 0.1.0
 */
interface A2uiCatalog {
    /** Protocol catalog identifier. @since 0.1.0 */
    val catalogId: String

    /** Components indexed by protocol name. @since 0.1.0 */
    val components: Map<String, A2uiComponent>
}

/** Creates a catalog from [components].
 * @param catalogId protocol catalog identifier.
 * @param components component implementations.
 * @since 0.1.0
 */
fun A2uiCatalog(
    catalogId: String,
    components: List<A2uiComponent>,
): A2uiCatalog =
    object : A2uiCatalog {
        override val catalogId = catalogId
        override val components = components.associateBy { it.name }
    }
