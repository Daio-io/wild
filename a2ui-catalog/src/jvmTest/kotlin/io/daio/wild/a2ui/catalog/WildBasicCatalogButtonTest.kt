// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiUserAction
import io.daio.wild.a2ui.compose.A2uiSurface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WildBasicCatalogButtonTest {
    @Test
    fun interactive_button_dispatches_button_clicked() =
        runComposeUiTest {
            val processor =
                processorWithComponents(
                    """
                    [{"id":"root","component":"Button","child":"label","action":{"event":{"name":"button_clicked"}}},
                     {"id":"label","component":"Text","text":"Continue"}]
                    """.trimIndent(),
                )
            val actions = mutableListOf<A2uiUserAction>()

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    wildA2uiBasicCatalogV1(),
                    processor,
                    onAction = actions::add,
                )
            }

            onNodeWithText("Continue").performClick()
            runOnIdle { assertEquals(A2uiUserAction("button_clicked", "main", "root"), actions.single()) }
        }

    @Test
    fun button_missing_action_noop() =
        runComposeUiTest {
            val processor =
                processorWithComponents(
                    """
                    [{"id":"root","component":"Button","child":"label"},
                     {"id":"label","component":"Text","text":"Continue"}]
                    """.trimIndent(),
                )
            val actions = mutableListOf<A2uiUserAction>()

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    wildA2uiBasicCatalogV1(),
                    processor,
                    onAction = actions::add,
                )
            }

            onNodeWithText("Continue").performClick()
            runOnIdle { assertTrue(actions.isEmpty()) }
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
