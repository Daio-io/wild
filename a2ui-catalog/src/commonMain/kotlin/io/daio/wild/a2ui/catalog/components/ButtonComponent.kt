// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.A2uiUserAction
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.button.Button
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI Button component.
 * Example: register [ButtonComponent] in the Basic Catalog.
 * @since 0.1.0
 */
object ButtonComponent : A2uiComponent {
    override val name = "Button"
    override val properties =
        listOf(
            A2uiProperty.componentId("child", required = true),
            A2uiProperty.action("action"),
        )

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val childId = props.raw("child")?.jsonPrimitive?.contentOrNull ?: return
        val event = props.raw("action")?.jsonObject?.get("event")?.jsonObject
        val eventName = event?.get("name")?.jsonPrimitive?.contentOrNull
        Button(
            onClick = {
                if (eventName == null) return@Button
                dispatchAction(
                    A2uiUserAction(
                        name = eventName,
                        surfaceId = surface.surfaceId,
                        sourceComponentId = props.componentId,
                        context = event["context"]?.jsonObject ?: JsonObject(emptyMap()),
                    ),
                )
            },
            modifier = modifier,
        ) {
            RenderChild(childId)
        }
    }
}
