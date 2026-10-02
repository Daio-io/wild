// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiProcessResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class A2uiFixturesTest {
    @Test
    fun simple_text_and_interactive_fixtures_process_ok() {
        val processor = A2uiMessageProcessor()

        val results = processor.processJsonl(A2uiGalleryFixtures.SIMPLE_TEXT) + processor.processJsonl(A2uiGalleryFixtures.INTERACTIVE)

        assertTrue(results.all { it is A2uiProcessResult.Success })
        assertEquals(setOf("gallery-simple-text", "gallery-interactive-button"), processor.surfaces.value.keys)
        assertTrue(processor.surfaces.value.values.all { "root" in it.components })
    }
}
