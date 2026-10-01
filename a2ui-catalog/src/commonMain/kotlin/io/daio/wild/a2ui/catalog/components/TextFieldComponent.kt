// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.text.TextField
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI TextField component.
 * Example: register [TextFieldComponent] in the Basic Catalog.
 * @since 0.1.0
 */
object TextFieldComponent : A2uiComponent {
    override val name = "TextField"
    override val properties = listOf(A2uiProperty.dynamicString("value", required = true))

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val value = props.raw("value")
        val path = (value as? JsonObject)?.get("path")?.jsonPrimitive?.contentOrNull
        val modelText = bindString(value) ?: ""
        val state = rememberTextFieldState(initialText = modelText)

        if (path != null) {
            LaunchedEffect(modelText) {
                if (state.text.toString() != modelText) {
                    state.edit { replace(0, length, modelText) }
                }
            }
            LaunchedEffect(state.text) {
                val text = state.text.toString()
                if (text != modelText) {
                    bindUpdater(path)(JsonPrimitive(text))
                }
            }
        }
        TextField(
            state = state,
            modifier = modifier,
            enabled = path != null,
            readOnly = path == null,
        )
    }
}
