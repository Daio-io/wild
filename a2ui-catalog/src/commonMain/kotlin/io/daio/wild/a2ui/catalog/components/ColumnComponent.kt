// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiComponentState
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.text.Text
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Composable
internal fun A2uiComponentScope.RenderChild(
    id: String,
    modifier: Modifier = Modifier,
) {
    when (val child = observeComponentState(id)) {
        A2uiComponentState.Loading -> CircularProgressIndicator(modifier)
        is A2uiComponentState.Error -> Text(child.message, modifier)
        is A2uiComponentState.Success -> io.daio.wild.a2ui.compose.A2uiComponent(child, modifier)
    }
}

internal fun kotlinx.serialization.json.JsonElement?.childIds(): List<String> =
    (this as? JsonArray)?.mapNotNull { it.jsonPrimitive.contentOrNull } ?: emptyList()

/** Renders the A2UI Column component. @since 0.1.0 */
object ColumnComponent : A2uiComponent {
    override val name = "Column"
    override val properties = emptyList<A2uiProperty<*>>()

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val vertical =
            when (props.raw("justify")?.jsonPrimitive?.contentOrNull) {
                "center" -> Arrangement.Center
                "end" -> Arrangement.Bottom
                "spaceBetween" -> Arrangement.SpaceBetween
                else -> Arrangement.Top
            }
        val horizontal =
            when (props.raw("align")?.jsonPrimitive?.contentOrNull) {
                "center" -> Alignment.CenterHorizontally
                "end" -> Alignment.End
                else -> Alignment.Start
            }
        Column(modifier, verticalArrangement = vertical, horizontalAlignment = horizontal) {
            props.raw("children").childIds().forEach { RenderChild(it) }
        }
    }
}
