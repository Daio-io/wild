// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull

/** Read-only state for an A2UI surface. */
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
    internal constructor(message: CreateSurface) : this(message.surfaceId, message.catalogId, message.theme, message.sendDataModel)

    private val _components = mutableMapOf<String, JsonObject>()
    override val components: Map<String, JsonObject> get() = _components
    override var dataModel: JsonElement = JsonObject(emptyMap())
    override var error: String? = null

    fun putComponents(items: List<JsonObject>) {
        items.forEach { item ->
            val id = item["id"] as? kotlinx.serialization.json.JsonPrimitive
            id?.contentOrNull?.let { _components[it] = item }
        }
    }

    fun snapshot(): MutableA2uiSurface =
        MutableA2uiSurface(surfaceId, catalogId, theme, sendDataModel).also {
            it._components.putAll(_components)
            it.dataModel = dataModel
            it.error = error
        }
}
