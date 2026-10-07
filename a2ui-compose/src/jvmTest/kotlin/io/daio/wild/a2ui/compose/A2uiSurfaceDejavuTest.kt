// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import dejavu.assertStable
import dejavu.resetRecompositionCounts
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import io.daio.wild.a2ui.A2uiMessageProcessor
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class A2uiSurfaceDejavuTest {
    @Test
    fun a2uiSurface_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            val processor = A2uiMessageProcessor()
            processor.processJson("""{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"test"}}""")
            processor.processJson(
                """
                {
                  "version": "v0.9.1",
                  "updateComponents": {
                    "surfaceId": "main",
                    "components": [{"id": "root", "component": "Text", "text": "Hello"}]
                  }
                }
                """.trimIndent(),
            )
            val catalog = A2uiCatalog("test", listOf(StubTextComponent))
            val surface = processor.surfaces.value.getValue("main")
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                A2uiSurface(surface, catalog, processor, modifier = Modifier.testTag("sut"))
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    private object StubTextComponent : A2uiComponent {
        override val name = "Text"
        override val properties = emptyList<A2uiProperty<*>>()

        @Composable
        override fun A2uiComponentScope.Content(
            props: A2uiComponentProperties,
            modifier: Modifier,
        ) {
            BasicText(bindString(props.raw("text")) ?: "", modifier)
        }
    }
}
