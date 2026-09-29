// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** Properties supplied to a component implementation. */
data class A2uiComponentProperties(
    private val map: Map<String, JsonElement>,
    val componentId: String,
) {
    fun raw(name: String): JsonElement? = map[name]

    companion object {
        fun from(
            json: JsonObject,
            componentId: String,
        ) = A2uiComponentProperties(json.filterKeys { it != "id" && it != "component" }, componentId)
    }
}
