// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DocModelTest {
    @Test
    fun `component doc DSL builds an immutable document around a compiled sample`() {
        val sample =
            componentSample(
                id = "button-basic",
                sourceFile = "Button.samples.kt",
                symbol = "BasicButtonSample",
            ) {}

        val doc =
            componentDoc(
                name = "Button",
                symbol = "io.daio.wild.components.button.Button",
                artifact = "io.daio.wild:button",
                importStatement = "import io.daio.wild.components.button.Button",
                category = "Action",
                platforms =
                    setOf(
                        Platform.Common,
                        Platform.Android,
                        Platform.Desktop,
                        Platform.Web,
                    ),
            ) {
                summary("An interactive button primitive.")
                keywords("button", "action")
                parameter("onClick", "Invoked when the button is clicked.")
                guidance {
                    doThis("Use a button for actions.")
                    avoid("Use a button for navigation.")
                }
                example(sample, "Basic", "A button with text content.")
            }

        assertEquals("Button", doc.name)
        assertEquals("An interactive button primitive.", doc.summary)
        assertEquals(listOf("button", "action"), doc.keywords)
        assertEquals(
            listOf(ParameterGuidance("onClick", "Invoked when the button is clicked.")),
            doc.parameters,
        )
        assertEquals(
            listOf(
                Guidance(recommended = true, description = "Use a button for actions."),
                Guidance(recommended = false, description = "Use a button for navigation."),
            ),
            doc.guidance,
        )
        assertEquals(
            listOf(
                DocExample(
                    sample = sample,
                    title = "Basic",
                    description = "A button with text content.",
                ),
            ),
            doc.examples,
        )
    }
}
