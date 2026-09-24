// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.search

import io.daio.wild.docs.model.ComponentDoc

data class SearchResult(
    val doc: ComponentDoc,
    val score: Int,
)

class DocSearch(
    private val docs: List<ComponentDoc>,
) {
    fun search(query: String): List<SearchResult> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) return emptyList()

        return docs
            .mapNotNull { doc ->
                score(doc, normalizedQuery)?.let { SearchResult(doc, it) }
            }.sortedWith(
                compareByDescending<SearchResult> { it.score }
                    .thenBy { it.doc.name },
            )
    }

    private fun score(
        doc: ComponentDoc,
        query: String,
    ): Int? {
        val name = doc.name.lowercase()
        val keywords = doc.keywords.map(String::lowercase)
        val searchableDescription =
            buildList {
                add(doc.summary)
                add(doc.category)
                addAll(doc.platforms.map { it.displayName })
            }.joinToString(" ").lowercase()

        return when {
            name == query -> 100
            query in keywords -> 90
            query in name -> 80
            keywords.any { query in it } -> 70
            query in searchableDescription -> 50
            else -> null
        }
    }
}
