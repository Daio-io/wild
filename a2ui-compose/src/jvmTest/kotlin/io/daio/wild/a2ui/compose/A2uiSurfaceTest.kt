// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiUserAction
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class A2uiSurfaceTest {
    @Test
    fun renders_literal_text_root() =
        runComposeUiTest {
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

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), catalog, processor)
            }

            onNodeWithText("Hello").assertTextEquals("Hello")
        }

    @Test
    fun renders_object_form_text_root() =
        runComposeUiTest {
            val processor = A2uiMessageProcessor()
            processor.processJson("""{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"test"}}""")
            processor.processJson(
                """
                {
                  "version": "v0.9.1",
                  "updateComponents": {
                    "surfaceId": "main",
                    "components": [{"id":"root","component":{"Text":{"text":"Hello"}}}]
                  }
                }
                """.trimIndent(),
            )
            val catalog = A2uiCatalog("test", listOf(StubTextComponent))

            setContent {
                A2uiSurface(processor.surfaces.value.getValue("main"), catalog, processor)
            }

            onNodeWithText("Hello").assertTextEquals("Hello")
        }

    @Test
    fun missing_child_shows_loading() =
        runComposeUiTest {
            val processor = processorWithRoot("Container", """"children":["missing"]""")
            val catalog = A2uiCatalog("test", listOf(StubContainerComponent))

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    catalog,
                    processor,
                    loading = { BasicText("loading") },
                )
            }

            onNodeWithText("loading").assertTextEquals("loading")
        }

    @Test
    fun catalog_mismatch_shows_error() =
        runComposeUiTest {
            val processor = processorWithRoot("Text", """"text":"Hello"""")
            val catalog = A2uiCatalog("other", listOf(StubTextComponent))

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    catalog,
                    processor,
                    error = { BasicText(it) },
                )
            }

            onNodeWithText("catalog mismatch").assertTextEquals("catalog mismatch")
        }

    @Test
    fun button_stub_dispatches_action() =
        runComposeUiTest {
            val processor = processorWithRoot("Button", """"label":"Send"""")
            val actions = mutableListOf<A2uiUserAction>()
            val catalog = A2uiCatalog("test", listOf(StubButtonComponent))

            setContent {
                A2uiSurface(
                    processor.surfaces.value.getValue("main"),
                    catalog,
                    processor,
                    onAction = actions::add,
                )
            }

            onNodeWithText("Send").performClick()
            runOnIdle { assertEquals(A2uiUserAction("send", "main", "root"), actions.single()) }
        }

    private fun processorWithRoot(
        type: String,
        properties: String,
    ): A2uiMessageProcessor {
        val processor = A2uiMessageProcessor()
        processor.processJson("""{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"test"}}""")
        processor.processJson(
            """{"version":"v0.9.1","updateComponents":{"surfaceId":"main","components":[{"id":"root","component":"$type",$properties}]}}""",
        )
        return processor
    }
}

private object StubTextComponent : A2uiComponent {
    override val name = "Text"
    override val properties = emptyList<A2uiProperty<*>>()

    @androidx.compose.runtime.Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: androidx.compose.ui.Modifier,
    ) {
        BasicText(bindString(props.raw("text")) ?: "", modifier)
    }
}

private object StubContainerComponent : A2uiComponent {
    override val name = "Container"
    override val properties = listOf(A2uiProperty.childList("children"))

    @androidx.compose.runtime.Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        A2uiComponent(
            observeComponentState(props.raw("children")!!.jsonArray.single().jsonPrimitive.content),
            modifier,
            loading = { BasicText("loading") },
        )
    }
}

private object StubButtonComponent : A2uiComponent {
    override val name = "Button"
    override val properties = emptyList<A2uiProperty<*>>()

    @androidx.compose.runtime.Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        BasicText(
            bindString(props.raw("label")) ?: "",
            modifier.clickable { dispatchAction(A2uiUserAction("send", surface.surfaceId, props.componentId)) },
        )
    }
}
