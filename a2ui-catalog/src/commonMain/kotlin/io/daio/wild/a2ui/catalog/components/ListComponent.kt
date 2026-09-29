// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI List component. @since 0.1.0 */
object ListComponent : A2uiComponent {
    override val name = "List"
    override val properties =
        listOf(
            A2uiProperty.childList("children", required = true),
            A2uiProperty.string("direction"),
        )

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val children = props.raw("children").childIds()
        if (props.raw("direction")?.jsonPrimitive?.contentOrNull == "horizontal") {
            LazyRow(modifier) { items(children) { RenderChild(it) } }
        } else {
            LazyColumn(modifier) { items(children) { RenderChild(it) } }
        }
    }
}
