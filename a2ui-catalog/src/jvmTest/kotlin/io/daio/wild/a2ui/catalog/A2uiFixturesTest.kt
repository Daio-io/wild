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

        val results = processor.processJsonl(SIMPLE_TEXT) + processor.processJsonl(INTERACTIVE)

        assertTrue(results.all { it is A2uiProcessResult.Success })
        assertEquals(setOf("gallery-simple-text", "gallery-interactive-button"), processor.surfaces.value.keys)
        assertTrue(processor.surfaces.value.values.all { "root" in it.components })
    }

    private companion object {
        const val SIMPLE_TEXT = """
            {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-simple-text","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
            {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-simple-text","components":[{"id":"root","component":"Text","text":"Hello, Wild A2UI!"}]}}
        """

        const val INTERACTIVE = """
            {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-interactive-button","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
            {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-interactive-button","components":[{"id":"root","component":"Column","children":["title","action_button"]},{"id":"title","component":"Text","text":"Click the button below"},{"id":"action_button","component":"Button","child":"button_label","action":{"event":{"name":"button_clicked","context":{}}}},{"id":"button_label","component":"Text","text":"Click Me"}]}}
        """
    }
}
