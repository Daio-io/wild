// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement

class A2uiMessageProcessor {
    private val _surfaces = MutableStateFlow<Map<String, A2uiSurfaceModel>>(emptyMap())
    val surfaces: StateFlow<Map<String, A2uiSurfaceModel>> = _surfaces.asStateFlow()

    fun processJson(json: String): A2uiProcessResult {
        val envelope =
            runCatching { jsonParser.decodeFromString<A2uiServerEnvelope>(json) }
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

    fun processJsonl(text: String): List<A2uiProcessResult> = text.lineSequence().filter { it.isNotBlank() }.map(::processJson).toList()

    private fun create(message: CreateSurface): A2uiProcessResult {
        if (message.surfaceId in _surfaces.value) {
            return A2uiProcessResult.Failure("surface exists: ${message.surfaceId}")
        }
        val surface =
            MutableA2uiSurface(
                message.surfaceId,
                message.catalogId,
                message.theme,
                message.sendDataModel,
            )
        _surfaces.value += message.surfaceId to surface
        return A2uiProcessResult.Success(message.surfaceId)
    }

    private fun updateComponents(message: UpdateComponents): A2uiProcessResult {
        val surface =
            _surfaces.value[message.surfaceId] as? MutableA2uiSurface
                ?: return A2uiProcessResult.Failure("missing surface: ${message.surfaceId}")
        surface.putComponents(message.components)
        _surfaces.value += message.surfaceId to surface
        return A2uiProcessResult.Success(message.surfaceId)
    }

    private fun updateDataModel(message: UpdateDataModel): A2uiProcessResult {
        val value = message.value ?: return A2uiProcessResult.Failure("missing data model value")
        return setPath(message.surfaceId, message.path ?: "", value)
    }

    private fun deleteSurface(message: DeleteSurface): A2uiProcessResult {
        if (message.surfaceId !in _surfaces.value) {
            return A2uiProcessResult.Failure("missing surface: ${message.surfaceId}")
        }
        _surfaces.value -= message.surfaceId
        return A2uiProcessResult.Success(message.surfaceId)
    }

    fun resolvePath(
        surfaceId: String,
        path: String,
    ): JsonElement? = JsonPointer.get(_surfaces.value[surfaceId]?.dataModel, path)

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
                surface.dataModel = result.value
                _surfaces.value += surfaceId to surface
                A2uiProcessResult.Success(surfaceId)
            }
        }
    }

    fun dispatchAction(action: A2uiUserAction): String = jsonParser.encodeToString(A2uiClientEnvelope(A2UI_VERSION_0_9_1, action))
}
