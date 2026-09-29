// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.compose

import kotlinx.serialization.json.JsonObject

/** Describes a property supported by an A2UI component. @since 0.1.0 */
class A2uiProperty<T> private constructor(
    val name: String,
    val required: Boolean,
) {
    companion object {
        /** Creates a string property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun string(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        /** Creates a dynamic string property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun dynamicString(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        /** Creates a boolean property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun bool(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<Boolean>(name, required)

        /** Creates a dynamic boolean property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun dynamicBool(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<Boolean>(name, required)

        /** Creates a component ID property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun componentId(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<String>(name, required)

        /** Creates a child-list property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun childList(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<List<String>>(name, required)

        /** Creates an action property. @param name property name. @param required whether it is required. @since 0.1.0 */
        fun action(
            name: String,
            required: Boolean = false,
        ) = A2uiProperty<JsonObject>(name, required)
    }
}
