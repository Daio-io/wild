// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import androidx.compose.runtime.Composable
import io.daio.wild.a2ui.A2uiMessageProcessor
import io.daio.wild.a2ui.A2uiSurfaceModel
import io.daio.wild.a2ui.A2uiUserAction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

interface A2uiComponentScope {
    val surface: A2uiSurfaceModel

    fun dispatchAction(action: A2uiUserAction)

    @Composable fun observeComponentState(id: String): A2uiComponentState

    fun bindString(el: JsonElement?): String?

    fun bindBoolean(path: String): Boolean

    fun bindUpdater(path: String): (JsonElement) -> Unit
}

internal class DefaultA2uiComponentScope(
    override val surface: A2uiSurfaceModel,
    private val processor: A2uiMessageProcessor,
    private val onAction: (A2uiUserAction) -> Unit,
) : A2uiComponentScope {
    override fun dispatchAction(action: A2uiUserAction) {
        onAction(action)
        processor.dispatchAction(action)
    }

    @Composable
    override fun observeComponentState(id: String): A2uiComponentState =
        surface.components[id]?.let { A2uiComponentState.Success(id, it) } ?: A2uiComponentState.Loading

    override fun bindString(el: JsonElement?): String? =
        when (el) {
            is JsonPrimitive -> el.contentOrNull
            is JsonObject ->
                el["path"]?.jsonPrimitive?.contentOrNull?.let {
                    processor.resolvePath(surface.surfaceId, it)?.jsonPrimitive?.contentOrNull
                }
            else -> null
        }

    override fun bindBoolean(path: String): Boolean = processor.resolvePath(surface.surfaceId, path)?.jsonPrimitive?.booleanOrNull ?: false

    override fun bindUpdater(path: String): (JsonElement) -> Unit =
        { value ->
            processor.setPath(surface.surfaceId, path, value)
        }
}
