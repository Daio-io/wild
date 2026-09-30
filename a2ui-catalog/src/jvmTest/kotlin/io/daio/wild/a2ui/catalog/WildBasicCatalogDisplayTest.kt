// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.onNodeWithContentDescription
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
                "Button",
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
    fun catalog_components_describe_their_protocol_properties() {
        val catalog = wildA2uiBasicCatalogV1()

        assertEquals(listOf("text"), catalog.components.getValue("Text").properties.map { it.name })
        assertEquals(listOf("children", "justify", "align"), catalog.components.getValue("Column").properties.map { it.name })
        assertEquals(listOf("children", "justify", "align"), catalog.components.getValue("Row").properties.map { it.name })
        assertEquals(listOf("child", "action"), catalog.components.getValue("Button").properties.map { it.name })
        assertEquals(listOf("child"), catalog.components.getValue("Card").properties.map { it.name })
        assertEquals(listOf("axis"), catalog.components.getValue("Divider").properties.map { it.name })
        assertEquals(listOf("name"), catalog.components.getValue("Icon").properties.map { it.name })
        assertEquals(listOf("children", "direction"), catalog.components.getValue("List").properties.map { it.name })
        assertEquals(true, catalog.components.getValue("Text").properties.single().required)
        assertEquals(true, catalog.components.getValue("Column").properties.first().required)
        assertEquals(true, catalog.components.getValue("Button").properties.first().required)
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

    @Test
    fun known_icon_renders_as_an_image() =
        runComposeUiTest {
            val processor = processorWithComponents("""[{"id":"root","component":"Icon","name":"circle"}]""")
            setContent { A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor) }
            onNodeWithContentDescription("circle").assertIsDisplayed()
        }

    @Test
    fun unresolved_card_child_shows_loading_indicator() =
        runComposeUiTest {
            val processor = processorWithComponents("""[{"id":"root","component":"Card","child":"missing"}]""")
            setContent { A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor) }
            onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
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
