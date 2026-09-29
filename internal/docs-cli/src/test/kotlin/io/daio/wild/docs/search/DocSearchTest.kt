// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.search

import io.daio.wild.docs.catalog.WildCatalog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocSearchTest {
    private val search = DocSearch(WildCatalog.components)

    @Test
    fun `finds Button by name and intent keywords`() {
        listOf("button", "press", "cta", "remote", "tv").forEach { query ->
            assertEquals("Button", search.search(query).first().doc.name)
        }
    }

    @Test
    fun `ranks an exact component name above descriptive matches`() {
        assertEquals(100, search.search("Button").first().score)
    }

    @Test
    fun `returns no results for unrelated intent`() {
        assertTrue(search.search("dialog").isEmpty())
    }
}
