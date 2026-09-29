// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** Properties supplied to a component implementation.
 * @param map raw protocol properties.
 * @param componentId component identifier.
 * @since 0.1.0
 */
data class A2uiComponentProperties(
    private val map: Map<String, JsonElement>,
    val componentId: String,
) {
    /** Returns a raw property by [name]. @param name property name. @since 0.1.0 */
    fun raw(name: String): JsonElement? = map[name]

    companion object {
        fun from(
            json: JsonObject,
            componentId: String,
        ) = A2uiComponentProperties(json.filterKeys { it != "id" && it != "component" }, componentId)
    }
}
