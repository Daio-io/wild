// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI Row component. @since 0.1.0 */
object RowComponent : A2uiComponent {
    override val name = "Row"
    override val properties = emptyList<A2uiProperty<*>>()

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val horizontal =
            when (props.raw("justify")?.jsonPrimitive?.contentOrNull) {
                "center" -> Arrangement.Center
                "end" -> Arrangement.End
                "spaceBetween" -> Arrangement.SpaceBetween
                else -> Arrangement.Start
            }
        val vertical =
            when (props.raw("align")?.jsonPrimitive?.contentOrNull) {
                "center" -> Alignment.CenterVertically
                "end" -> Alignment.Bottom
                else -> Alignment.Top
            }
        Row(modifier, horizontalArrangement = horizontal, verticalAlignment = vertical) {
            props.raw("children").childIds().forEach { RenderChild(it) }
        }
    }
}
