// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

interface A2uiSurfaceModel {
    val surfaceId: String
    val catalogId: String
    val theme: JsonObject?
    val sendDataModel: Boolean
    val components: Map<String, JsonObject>
    val dataModel: JsonElement
    val error: String?
}

internal class MutableA2uiSurface(
    override val surfaceId: String,
    override val catalogId: String,
    override val theme: JsonObject?,
    override val sendDataModel: Boolean,
) : A2uiSurfaceModel {
    private val _components = mutableMapOf<String, JsonObject>()
    override val components: Map<String, JsonObject> get() = _components
    override var dataModel: JsonElement = JsonObject(emptyMap())
    override var error: String? = null

    fun putComponents(items: List<JsonObject>) {
        items.forEach { item ->
            item["id"]?.jsonPrimitive?.contentOrNull?.let { _components[it] = item }
        }
    }
}
