// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal object JsonPointer {
    fun get(
        root: JsonElement?,
        path: String,
    ): JsonElement? {
        if (path.isEmpty() || path == "/") return root
        if (!path.startsWith('/')) return null
        var current = root
        path.removePrefix("/").split('/').forEach { encoded ->
            current =
                when (current) {
                    is JsonObject -> current[decode(encoded)]
                    is JsonArray -> return null
                    else -> return null
                }
        }
        return current
    }

    sealed class SetResult {
        data class Success(val value: JsonElement) : SetResult()

        data object Failure : SetResult()
    }

    fun set(
        root: JsonElement,
        path: String,
        value: JsonElement,
    ): SetResult {
        if (path.isEmpty() || path == "/") return SetResult.Success(value)
        if (!path.startsWith('/')) return SetResult.Failure
        return setObject(root, path.removePrefix("/").split('/').map(::decode), value)
    }

    private fun setObject(
        root: JsonElement,
        segments: List<String>,
        value: JsonElement,
    ): SetResult {
        if (root is JsonArray || root !is JsonObject && root !is JsonNull) return SetResult.Failure
        val objectRoot = root as? JsonObject ?: JsonObject(emptyMap())
        val key = segments.first()
        val updated =
            if (segments.size == 1) {
                merge(objectRoot, key, value)
            } else {
                when (val child = setObject(objectRoot[key] ?: JsonObject(emptyMap()), segments.drop(1), value)) {
                    is SetResult.Failure -> return SetResult.Failure
                    is SetResult.Success ->
                        merge(objectRoot, key, child.value)
                }
            }
        return SetResult.Success(updated)
    }

    private fun merge(
        root: JsonObject,
        key: String,
        value: JsonElement,
    ): JsonObject =
        buildJsonObject {
            root.forEach { (existingKey, existingValue) -> put(existingKey, existingValue) }
            put(key, value)
        }

    private fun decode(segment: String) = segment.replace("~1", "/").replace("~0", "~")
}
