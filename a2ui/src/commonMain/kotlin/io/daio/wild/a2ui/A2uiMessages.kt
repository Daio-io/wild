// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
/**
 * A server envelope containing exactly one A2UI message.
 *
 * @param version protocol version.
 * @param createSurface surface creation message, if present.
 * @param updateComponents component update message, if present.
 * @param updateDataModel data-model update message, if present.
 * @param deleteSurface surface deletion message, if present.
 * @since 0.1.0
 */
data class A2uiServerEnvelope(
    val version: String,
    val createSurface: CreateSurface? = null,
    val updateComponents: UpdateComponents? = null,
    val updateDataModel: UpdateDataModel? = null,
    val deleteSurface: DeleteSurface? = null,
)

@Serializable
/**
 * Creates an empty surface.
 *
 * @param surfaceId surface identifier.
 * @param catalogId component catalog.
 * @param theme optional surface theme.
 * @param sendDataModel whether data-model updates should be sent.
 * @since 0.1.0
 */
data class CreateSurface(
    val surfaceId: String,
    val catalogId: String,
    val theme: JsonObject? = null,
    val sendDataModel: Boolean = false,
)

@Serializable
/**
 * Replaces or adds component definitions on a surface.
 *
 * @param surfaceId target surface.
 * @param components component definitions.
 * @since 0.1.0
 */
data class UpdateComponents(val surfaceId: String, val components: List<JsonObject>)

@Serializable
/**
 * Updates a JSON Pointer path in a surface data model.
 *
 * @param surfaceId target surface.
 * @param path JSON Pointer.
 * @param value replacement value.
 * @since 0.1.0
 */
data class UpdateDataModel(
    val surfaceId: String,
    val path: String? = null,
    val value: JsonElement? = null,
)

@Serializable
/**
 * Deletes a surface.
 *
 * @param surfaceId surface identifier.
 * @since 0.1.0
 */
data class DeleteSurface(val surfaceId: String)

@Serializable
/**
 * Describes an action initiated by a client component.
 *
 * @param name action name.
 * @param surfaceId source surface.
 * @param sourceComponentId originating component, if known.
 * @param context action context values.
 * @since 0.1.0
 */
data class A2uiUserAction(
    val name: String,
    val surfaceId: String,
    val sourceComponentId: String? = null,
    val context: JsonObject = JsonObject(emptyMap()),
)

/**
 * Result of processing one A2UI envelope.
 *
 * @since 0.1.0
 */
sealed class A2uiProcessResult {
    /** A successful processing result.
     * @param surfaceId affected surface.
     * @since 0.1.0
     */
    data class Success(val surfaceId: String) : A2uiProcessResult()

    /** A failed processing result.
     * @param message failure description.
     * @since 0.1.0
     */
    data class Failure(val message: String) : A2uiProcessResult()
}

@Serializable
internal data class A2uiClientEnvelope(
    val version: String,
    val userAction: A2uiUserAction,
)
