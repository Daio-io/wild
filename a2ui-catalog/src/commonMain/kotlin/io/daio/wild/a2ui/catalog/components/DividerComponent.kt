// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.layout.divider.VerticalDivider
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI Divider component. @since 0.1.0 */
object DividerComponent : A2uiComponent {
    override val name = "Divider"
    override val properties = listOf(A2uiProperty.string("axis"))

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        if (props.raw("axis")?.jsonPrimitive?.contentOrNull == "vertical") {
            VerticalDivider(modifier)
        } else {
            HorizontalDivider(modifier)
        }
    }
}
