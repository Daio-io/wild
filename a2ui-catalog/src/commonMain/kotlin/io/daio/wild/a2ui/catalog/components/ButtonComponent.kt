// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.A2uiUserAction
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiComponentState
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.button.Button
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.text.Text
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ButtonComponent : A2uiComponent {
    override val name = "Button"
    override val properties = emptyList<A2uiProperty<*>>()

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val childId = props.raw("child")?.jsonPrimitive?.contentOrNull ?: return
        val eventName =
            props.raw("action")?.jsonObject?.get("event")?.jsonObject
                ?.get("name")?.jsonPrimitive?.contentOrNull
        Button(
            onClick = {
                if (eventName == null) return@Button
                dispatchAction(
                    A2uiUserAction(
                        name = eventName,
                        surfaceId = surface.surfaceId,
                        sourceComponentId = props.componentId,
                        context =
                            props.raw("action")?.jsonObject?.get("event")?.jsonObject
                                ?.get("context")?.jsonObject ?: JsonObject(emptyMap()),
                    ),
                )
            },
            modifier = modifier,
        ) {
            when (val child = observeComponentState(childId)) {
                A2uiComponentState.Loading -> CircularProgressIndicator()
                is A2uiComponentState.Error -> Text(child.message)
                is A2uiComponentState.Success -> A2uiComponent(child)
            }
        }
    }
}
