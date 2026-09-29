// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.compose.A2uiSurface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class WildBasicCatalogDisplayTest {
    @Test
    fun factory_exposes_the_basic_catalog_components() {
        val catalog = wildA2uiBasicCatalogV1()

        assertEquals(
            "https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json",
            catalog.catalogId,
        )
        assertEquals(
            listOf(
                "Text",
                "Column",
                "Row",
                "Card",
                "Divider",
                "Icon",
                "List",
                "Image",
                "Video",
                "AudioPlayer",
                "Modal",
                "Tabs",
                "Slider",
                "DateTimeInput",
                "ChoicePicker",
            ),
            catalog.components.keys.toList(),
        )
        assertFalse(catalog.components.containsKey("Button"))
        assertFalse(catalog.components.containsKey("CheckBox"))
        assertFalse(catalog.components.containsKey("TextField"))
    }

    @Test
    fun text_column_renders() =
        runComposeUiTest {
            val processor =
                processorWithComponents(
                    """
                    [{"id":"root","component":"Column","children":["message"]},
                     {"id":"message","component":"Text","text":"Hello from A2UI"}]
                    """.trimIndent(),
                )

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    wildA2uiBasicCatalogV1(),
                    processor,
                )
            }

            onNodeWithText("Hello from A2UI").assertTextEquals("Hello from A2UI")
        }

    @Test
    fun image_stub_is_unsupported() =
        runComposeUiTest {
            val processor =
                processorWithComponents(
                    """[{"id":"root","component":"Image"}]""",
                )

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    wildA2uiBasicCatalogV1(),
                    processor,
                )
            }

            onNodeWithText("Unsupported: Image").assertTextEquals("Unsupported: Image")
        }

    private fun processorWithComponents(components: String): A2uiMessageProcessor {
        val processor = A2uiMessageProcessor()
        processor.processJson(
            """{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"$WILD_BASIC_CATALOG_ID"}}""",
        )
        processor.processJson(
            """{"version":"v0.9.1","updateComponents":{"surfaceId":"main","components":$components}}""",
        )
        return processor
    }
}
