// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class A2uiMessageProcessorTest {
    @Test
    fun createSurface_thenUpdateComponents_storesRoot() {
        val processor = A2uiMessageProcessor()

        assertTrue(
            processor.processJson(
                """
                {"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"catalog"}}
                """.trimIndent(),
            ) is A2uiProcessResult.Success,
        )
        assertTrue(
            processor.processJson(
                """
                {"version":"v0.9.1","updateComponents":{"surfaceId":"main","components":[{"id":"root","component":{"Text":{"text":"Hello"}}}]}}
                """.trimIndent(),
            ) is A2uiProcessResult.Success,
        )

        assertEquals(
            "{\"id\":\"root\",\"component\":{\"Text\":{\"text\":\"Hello\"}}}",
            processor.surfaces.value["main"]?.components?.get("root")?.toString(),
        )
    }

    @Test
    fun createSurface_duplicate_fails_without_mutation_and_delete_then_recreate_succeeds() {
        val processor = A2uiMessageProcessor()
        val create = """{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"catalog"}}"""

        assertIs<A2uiProcessResult.Success>(processor.processJson(create))
        assertIs<A2uiProcessResult.Failure>(processor.processJson(create))
        assertEquals(1, processor.surfaces.value.size)

        assertIs<A2uiProcessResult.Success>(processor.processJson("""{"version":"v0.9.1","deleteSurface":{"surfaceId":"main"}}"""))
        assertFalse(processor.surfaces.value.containsKey("main"))
        assertIs<A2uiProcessResult.Success>(processor.processJson(create))
    }

    @Test
    fun updateDataModel_setsJsonPointer() {
        val processor = A2uiMessageProcessor()
        processor.processJson("""{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"catalog"}}""")

        assertIs<A2uiProcessResult.Success>(
            processor.processJson(
                """
                {"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/foo","value":"bar"}}
                """.trimIndent(),
            ),
        )

        assertEquals("bar", processor.resolvePath("main", "/foo")?.toString()?.trim('"'))
    }

    @Test
    fun reject_v1_0_version() {
        val processor = A2uiMessageProcessor()

        assertIs<A2uiProcessResult.Failure>(
            processor.processJson(
                """
                {"version":"v1.0","createSurface":{"surfaceId":"main","catalogId":"catalog"}}
                """.trimIndent(),
            ),
        )
        assertTrue(processor.surfaces.value.isEmpty())
    }

    @Test
    fun processJson_rejects_multiple_envelope_keys() {
        val processor = A2uiMessageProcessor()

        assertIs<A2uiProcessResult.Failure>(
            processor.processJson(
                """
                {"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"catalog"},"deleteSurface":{"surfaceId":"main"}}
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun updateDataModel_does_not_modify_when_pointer_enters_array() {
        val processor = A2uiMessageProcessor()
        processor.processJson("""{"version":"v0.9.1","createSurface":{"surfaceId":"main","catalogId":"catalog"}}""")
        processor.processJson("""{"version":"v0.9.1","updateDataModel":{"surfaceId":"main","path":"/items","value":[1,2]}}""")

        assertIs<A2uiProcessResult.Failure>(processor.setPath("main", "/items/0", kotlinx.serialization.json.JsonPrimitive(3)))
        assertEquals("[1,2]", processor.resolvePath("main", "/items")?.toString())
    }

    @Test
    fun dispatchAction_builds_client_envelope() {
        val processor = A2uiMessageProcessor()

        assertEquals(
            "{\"version\":\"v0.9.1\",\"userAction\":{\"name\":\"submit\",\"surfaceId\":\"main\"}}",
            processor.dispatchAction(A2uiUserAction("submit", "main")),
        )
    }
}
