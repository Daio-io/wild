// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ChildListTest {
    @Test
    fun explicit_child_list_objects_resolve_ids() {
        val element = Json.parseToJsonElement("{\"explicitList\":[\"first\",\"second\"]}")

        assertEquals(listOf("first", "second"), element.childIds())
        assertEquals(emptyList(), Json.parseToJsonElement("{\"template\":\"item\"}").childIds())
    }
}
