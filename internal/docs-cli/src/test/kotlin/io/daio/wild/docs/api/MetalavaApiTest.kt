// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.docs.api

import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetalavaApiTest {
    @Test
    fun `reads Button parameters without splitting generic function types`() {
        val api = MetalavaApi.parse(buttonApiText())
        val button = assertNotNull(api.function("Button"))

        assertEquals(
            listOf(
                "onClick",
                "modifier",
                "enabled",
                "onLongClick",
                "onDoubleClick",
                "style",
                "contentPadding",
                "interactionSource",
                "content",
            ),
            button.parameters.map { it.name },
        )
        assertFalse(button.parameters.first { it.name == "onClick" }.optional)
        assertTrue(button.parameters.first { it.name == "modifier" }.optional)
        assertFalse(button.parameters.first { it.name == "content" }.optional)
    }

    private fun buttonApiText(): String =
        Path
            .of(
                System.getProperty("wild.repoRoot"),
                "components",
                "button",
                "api",
                "api.txt",
            ).readText()
}
