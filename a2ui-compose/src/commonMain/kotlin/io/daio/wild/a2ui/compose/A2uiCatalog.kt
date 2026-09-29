// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

/** A named collection of A2UI component implementations. */
interface A2uiCatalog {
    val catalogId: String
    val components: Map<String, A2uiComponent>
}

fun A2uiCatalog(
    catalogId: String,
    components: List<A2uiComponent>,
): A2uiCatalog =
    object : A2uiCatalog {
        override val catalogId = catalogId
        override val components = components.associateBy { it.name }
    }
