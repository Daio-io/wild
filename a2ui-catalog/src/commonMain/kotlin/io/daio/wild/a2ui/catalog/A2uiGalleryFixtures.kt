// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog

/** Shared JSONL fixtures used by the A2UI gallery and catalog tests.
 *
 * Example: `processor.processJsonl(A2uiGalleryFixtures.SIMPLE_TEXT)`
 *
 * @since 0.1.0
 */
object A2uiGalleryFixtures {
    const val SIMPLE_TEXT = """
        {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-simple-text","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
        {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-simple-text","components":[{"id":"root","component":"Text","text":"Hello, Wild A2UI!"}]}}
    """
    const val INTERACTIVE_BUTTON = """
        {"version":"v0.9.1","createSurface":{"surfaceId":"gallery-interactive-button","catalogId":"https://a2ui.org/specification/v0_9_1/catalogs/basic/catalog.json"}}
        {"version":"v0.9.1","updateComponents":{"surfaceId":"gallery-interactive-button","components":[{"id":"root","component":"Column","children":["title","action_button"]},{"id":"title","component":"Text","text":"Click the button below"},{"id":"action_button","component":"Button","child":"button_label","action":{"event":{"name":"button_clicked","context":{}}}},{"id":"button_label","component":"Text","text":"Click Me"}]}}
    """
}
