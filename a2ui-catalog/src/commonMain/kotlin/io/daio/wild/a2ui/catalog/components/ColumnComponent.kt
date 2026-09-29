// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiChildList
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty

/** Renders the A2UI Column component. @since 0.1.0 */
object ColumnComponent : A2uiComponent {
    override val name = "Column"
    override val properties =
        listOf(
            A2uiProperty.childList("children", required = true),
            A2uiProperty.string("justify"),
            A2uiProperty.string("align"),
        )

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val vertical = verticalArrangement(props.stringValue("justify"))
        val horizontal = horizontalAlignment(props.stringValue("align"))
        Column(modifier, verticalArrangement = vertical, horizontalAlignment = horizontal) {
            when (val children = resolveChildList(props.raw("children"))) {
                is A2uiChildList.Ids -> children.ids.forEach { RenderChild(it) }
                A2uiChildList.Invalid, A2uiChildList.TemplatesUnsupported -> Unit
            }
        }
    }
}
