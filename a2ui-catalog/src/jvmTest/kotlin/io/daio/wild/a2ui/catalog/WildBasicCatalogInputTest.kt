// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.compose.A2uiSurface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

@OptIn(ExperimentalTestApi::class)
class WildBasicCatalogInputTest {
    @Test
    fun catalog_has_18_names() {
        assertEquals(
            listOf(
                "Text",
                "Button",
                "Column",
                "Row",
                "Card",
                "Divider",
                "Icon",
                "CheckBox",
                "TextField",
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
            wildA2uiBasicCatalogV1().components.keys.toList(),
        )
    }

    @Test
    fun checkbox_path_writes_model() =
        runComposeUiTest {
            val processor =
                processorWithComponents("""[{"id":"root","component":"CheckBox","label":"Agree","value":{"path":"/accepted"}}]""")
            processor.processJson(
                """{"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/accepted","value":false}}""",
            )

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor)
            }

            onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)).performClick()
            runOnIdle {
                assertEquals(true, processor.resolvePath("main", "/accepted")?.toString()?.toBoolean())
            }
        }

    @Test
    fun textField_literal_readonly() =
        runComposeUiTest {
            val processor = processorWithComponents("""[{"id":"root","component":"TextField","value":"Fixed"}]""")

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor)
            }

            onNodeWithText("Fixed").assertIsNotEnabled()
            assertFails { onNodeWithText("Fixed").performTextInput(" changed") }
            runOnIdle { assertEquals(null, processor.resolvePath("main", "/value")) }
        }

    @Test
    fun textField_path_writes_model() =
        runComposeUiTest {
            val processor = processorWithComponents("""[{"id":"root","component":"TextField","value":{"path":"/name"}}]""")
            processor.processJson(
                """{"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/name","value":"Initial"}}""",
            )

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor)
            }

            onNodeWithText("Initial").performTextInput(" changed")
            runOnIdle { assertEquals("\"Initial changed\"", processor.resolvePath("main", "/name").toString()) }
        }

    @Test
    fun textField_path_reflects_later_model_updates() =
        runComposeUiTest {
            val processor = processorWithComponents("""[{"id":"root","component":"TextField","value":{"path":"/name"}}]""")
            processor.processJson(
                """{"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/name","value":"Initial"}}""",
            )

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), wildA2uiBasicCatalogV1(), processor)
            }

            processor.processJson(
                """{"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/name","value":"Updated"}}""",
            )
            onNodeWithText("Updated")
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
