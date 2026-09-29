// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/** Processes A2UI server envelopes and publishes current surface state. */
class A2uiMessageProcessor {
    private val _surfaces = MutableStateFlow<Map<String, A2uiSurfaceModel>>(emptyMap())

    /**
     * Current surface snapshots keyed by surface ID.
     *
     * @since 0.1.0
     */
    val surfaces: StateFlow<Map<String, A2uiSurfaceModel>> = _surfaces.asStateFlow()

    /**
     * Processes one JSON A2UI envelope.
     *
     * @param json the envelope.
     * @return the processing result.
     * @since 0.1.0
     */
    fun processJson(json: String): A2uiProcessResult {
        val envelopeJson =
            runCatching { jsonParser.parseToJsonElement(json) as? JsonObject ?: return A2uiProcessResult.Failure("parse") }
                .getOrElse { return A2uiProcessResult.Failure(it.message ?: "parse") }
        val envelope =
            runCatching { jsonParser.decodeFromJsonElement<A2uiServerEnvelope>(envelopeJson) }
                .getOrElse { return A2uiProcessResult.Failure(it.message ?: "parse") }
        if (envelope.version !in setOf(A2UI_VERSION_0_9, A2UI_VERSION_0_9_1)) {
            return A2uiProcessResult.Failure("unsupported version: ${envelope.version}")
        }
        val messages =
            listOfNotNull(
                envelope.createSurface,
                envelope.updateComponents,
                envelope.updateDataModel,
                envelope.deleteSurface,
            )
        if (messages.size != 1) return A2uiProcessResult.Failure("expected exactly one envelope key")
        return when {
            envelope.createSurface != null -> create(envelope.createSurface)
            envelope.updateComponents != null -> updateComponents(envelope.updateComponents)
            envelope.updateDataModel != null -> updateDataModel(envelope.updateDataModel)
            envelope.deleteSurface != null -> deleteSurface(envelope.deleteSurface)
            else -> A2uiProcessResult.Failure("unsupported message")
        }
    }

    /** Processes newline-delimited JSON envelopes. @param text the JSONL input. @return one result per non-blank line. @since 0.1.0 */
    fun processJsonl(text: String): List<A2uiProcessResult> = text.lineSequence().filter { it.isNotBlank() }.map(::processJson).toList()

    private fun create(message: CreateSurface): A2uiProcessResult {
        if (message.surfaceId in _surfaces.value) {
            return A2uiProcessResult.Failure("surface exists: ${message.surfaceId}")
        }
        val surface =
            MutableA2uiSurface(message)
        _surfaces.value += message.surfaceId to surface
        return A2uiProcessResult.Success(message.surfaceId)
    }

    private fun updateComponents(message: UpdateComponents): A2uiProcessResult {
        val surface =
            _surfaces.value[message.surfaceId] as? MutableA2uiSurface
                ?: return A2uiProcessResult.Failure("missing surface: ${message.surfaceId}")
        val snapshot = surface.snapshot()
        snapshot.putComponents(message.components)
        _surfaces.value += message.surfaceId to snapshot
        return A2uiProcessResult.Success(message.surfaceId)
    }

    private fun updateDataModel(message: UpdateDataModel): A2uiProcessResult {
        return setPath(message.surfaceId, message.path ?: "", message.value ?: kotlinx.serialization.json.JsonNull)
    }

    private fun deleteSurface(message: DeleteSurface): A2uiProcessResult {
        if (message.surfaceId !in _surfaces.value) {
            return A2uiProcessResult.Failure("missing surface: ${message.surfaceId}")
        }
        _surfaces.value -= message.surfaceId
        return A2uiProcessResult.Success(message.surfaceId)
    }

    /**
     * Reads a JSON Pointer from a surface data model.
     *
     * @param surfaceId the surface.
     * @param path an RFC 6901 pointer.
     * @return the value, or null if absent.
     * @since 0.1.0
     */
    fun resolvePath(
        surfaceId: String,
        path: String,
    ): JsonElement? = JsonPointer.get(_surfaces.value[surfaceId]?.dataModel, path)

    /**
     * Sets a JSON Pointer in a surface data model.
     *
     * @param surfaceId the surface.
     * @param path an RFC 6901 pointer.
     * @param value the JSON value.
     * @return the processing result.
     * @since 0.1.0
     */
    fun setPath(
        surfaceId: String,
        path: String,
        value: JsonElement,
    ): A2uiProcessResult {
        val surface =
            _surfaces.value[surfaceId] as? MutableA2uiSurface
                ?: return A2uiProcessResult.Failure("missing surface: $surfaceId")
        return when (val result = JsonPointer.set(surface.dataModel, path, value)) {
            is JsonPointer.SetResult.Failure -> A2uiProcessResult.Failure("invalid data model path: $path")
            is JsonPointer.SetResult.Success -> {
                val snapshot = surface.snapshot()
                snapshot.dataModel = result.value
                _surfaces.value += surfaceId to snapshot
                A2uiProcessResult.Success(surfaceId)
            }
        }
    }

    /**
     * Encodes a client action envelope.
     *
     * @param action the action.
     * @return the JSON envelope.
     * @since 0.1.0
     */
    fun dispatchAction(action: A2uiUserAction): String = jsonParser.encodeToString(A2uiClientEnvelope(A2UI_VERSION_0_9_1, action))
}
