// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class A2uiServerEnvelope(
    val version: String,
    val createSurface: CreateSurface? = null,
    val updateComponents: UpdateComponents? = null,
    val updateDataModel: UpdateDataModel? = null,
    val deleteSurface: DeleteSurface? = null,
)

@Serializable
data class CreateSurface(
    val surfaceId: String,
    val catalogId: String,
    val theme: JsonObject? = null,
    val sendDataModel: Boolean = false,
)

@Serializable
data class UpdateComponents(val surfaceId: String, val components: List<JsonObject>)

@Serializable
data class UpdateDataModel(
    val surfaceId: String,
    val path: String? = null,
    val value: JsonElement? = null,
)

@Serializable
data class DeleteSurface(val surfaceId: String)

@Serializable
data class A2uiUserAction(
    val name: String,
    val surfaceId: String,
    val sourceComponentId: String? = null,
    val context: JsonObject = JsonObject(emptyMap()),
)

sealed class A2uiProcessResult {
    data class Success(val surfaceId: String) : A2uiProcessResult()

    data class Failure(val message: String) : A2uiProcessResult()
}

@Serializable
internal data class A2uiClientEnvelope(
    val version: String,
    val userAction: A2uiUserAction,
)
