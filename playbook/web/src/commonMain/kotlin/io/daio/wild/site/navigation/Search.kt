// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.navigation

data class SearchEntry(
    val label: String,
    val route: Route,
    val keywords: List<String>,
)

val searchIndex =
    listOf(
        SearchEntry(
            "Getting Started",
            Route.GettingStarted,
            listOf("install", "setup", "begin", "start"),
        ),
        SearchEntry(
            "Button",
            Route.Component.Button,
            listOf("click", "press", "interactive", "action"),
        ),
        SearchEntry(
            "Container",
            Route.Component.Container,
            listOf("box", "layout", "surface", "card"),
        ),
        SearchEntry(
            "Text",
            Route.Component.Text,
            listOf("label", "string", "typography", "font"),
        ),
        SearchEntry(
            "Icon",
            Route.Component.Icon,
            listOf("image", "vector", "symbol", "glyph"),
        ),
        SearchEntry(
            "ListItem",
            Route.Component.ListItem,
            listOf("list", "row", "item", "menu"),
        ),
        SearchEntry(
            "Toggleable",
            Route.Component.Toggleable,
            listOf("checkbox", "switch", "toggle", "select", "radio"),
        ),
        SearchEntry(
            "Divider",
            Route.Component.Divider,
            listOf("separator", "line", "horizontal", "vertical"),
        ),
        SearchEntry(
            "Style",
            Route.Foundation.Style,
            listOf("theme", "color", "border", "scale", "alpha"),
        ),
        SearchEntry(
            "Content Color",
            Route.Foundation.ContentColor,
            listOf("local", "composition", "inherit", "propagate"),
        ),
        SearchEntry(
            "Modifier",
            Route.Foundation.Modifier,
            listOf("thenIf", "conditional", "utility"),
        ),
        SearchEntry(
            "Interaction State",
            Route.Foundation.InteractionState,
            listOf("hover", "focus", "press", "selected"),
        ),
    )

fun searchPages(query: String): List<SearchEntry> {
    if (query.isBlank()) return emptyList()
    val lower = query.lowercase()
    return searchIndex.filter { entry ->
        entry.label.lowercase().contains(lower) ||
            entry.keywords.any { it.contains(lower) }
    }
}
