// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiProcessResult
import io.daio.wild.a2ui.catalog.wildA2uiBasicCatalogV1
import io.daio.wild.a2ui.compose.A2uiSurface
import io.daio.wild.components.text.Text
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform

private const val SIMPLE_TEXT = """
    {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-simple-text","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
    {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-simple-text","components":[{"id":"root","component":"Text","text":"Hello, Wild A2UI!"}]}}
"""

private const val INTERACTIVE = """
    {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-interactive-button","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
    {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-interactive-button","components":[{"id":"root","component":"Column","children":["title","action_button"]},{"id":"title","component":"Text","text":"Click the button below"},{"id":"action_button","component":"Button","child":"button_label","action":{"event":{"name":"button_clicked","context":{}}}},{"id":"button_label","component":"Text","text":"Click Me"}]}}
"""

@Composable
fun A2uiPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = A2uiPageDefaults.data,
) {
    val processor = remember { A2uiMessageProcessor() }
    val catalog = remember { wildA2uiBasicCatalogV1() }
    var loadError by remember { mutableStateOf<String?>(null) }
    var lastAction by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loadError =
            (processor.processJsonl(SIMPLE_TEXT) + processor.processJsonl(INTERACTIVE))
                .filterIsInstance<A2uiProcessResult.Failure>()
                .firstOrNull()
                ?.message
    }

    val surfaces by processor.surfaces.collectAsState()
    val galleryData =
        data.copy(
            demos =
                listOf(
                    Demo("v0.9.1 fixtures", "Rendered with wildA2uiBasicCatalogV1.") {
                        when {
                            loadError != null -> Text("A2UI load failed: $loadError")
                            surfaces.isEmpty() -> Text("No A2UI surfaces")
                            else -> {
                                Column {
                                    surfaces.values.forEach { surface ->
                                        key(surface.surfaceId) {
                                            if (surface.catalogId != catalog.catalogId) {
                                                Text("Catalog mismatch: ${surface.catalogId}")
                                            } else {
                                                A2uiSurface(
                                                    surface = surface,
                                                    catalog = catalog,
                                                    processor = processor,
                                                    onAction = { lastAction = it.name },
                                                )
                                            }
                                        }
                                    }
                                    lastAction?.let { Text("Last action: $it") }
                                }
                            }
                        }
                    },
                ),
        )

    ComponentPage(data = galleryData, modifier = modifier)
}

object A2uiPageDefaults {
    val data =
        ComponentPageData(
            name = "A2UI",
            description = "Agent-to-UI v0.9.1 surfaces rendered with Wild components.",
            module = "io.daio.wild:a2ui-catalog",
            demos = emptyList(),
            usage = "processor.processJsonl(fixture); A2uiSurface(surface, wildA2uiBasicCatalogV1(), processor)",
            props = emptyList(),
            platforms = listOf(Platform.Android, Platform.Desktop, Platform.Web),
        )
}
