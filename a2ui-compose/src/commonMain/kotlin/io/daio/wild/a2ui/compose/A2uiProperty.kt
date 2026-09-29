// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import kotlinx.serialization.json.JsonObject

/** Describes a property supported by an A2UI component. */
class A2uiProperty<T> private constructor(
    val name: String,
    val required: Boolean,
) {
    companion object {
        fun string(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        fun dynamicString(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        fun bool(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<Boolean>(name, required)

        fun dynamicBool(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<Boolean>(name, required)

        fun componentId(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        fun childList(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<List<String>>(name, required)

        fun action(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<JsonObject>(name, required)
    }
}
