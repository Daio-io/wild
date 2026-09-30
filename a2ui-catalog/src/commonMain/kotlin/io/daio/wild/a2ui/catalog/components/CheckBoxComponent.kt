// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.text.Text
import io.daio.wild.components.toggleable.Checkbox
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI CheckBox component. @since 0.1.0 */
object CheckBoxComponent : A2uiComponent {
    override val name = "CheckBox"
    override val properties = emptyList<A2uiProperty<*>>()

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val label = bindString(props.raw("label")) ?: ""
        val value = props.raw("value")
        val path = (value as? JsonObject)?.get("path")?.jsonPrimitive?.contentOrNull

        if (path != null) {
            Checkbox(
                checked = bindBoolean(path),
                onCheckedChange = { bindUpdater(path)(JsonPrimitive(it)) },
                enabled = true,
                modifier = modifier,
            )
        } else {
            Checkbox(
                checked = (value as? JsonPrimitive)?.booleanOrNull ?: false,
                onCheckedChange = {},
                enabled = false,
                modifier = modifier,
            )
        }
        Text(label)
    }
}
