// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.container.Container
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Renders the A2UI Card component. @since 0.1.0 */
object CardComponent : A2uiComponent {
    override val name = "Card"
    override val properties = listOf(A2uiProperty.componentId("child", required = true))

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val childId = props.raw("child")?.jsonPrimitive?.contentOrNull
        if (childId == null) {
            CircularProgressIndicator(modifier)
            return
        }
        Container(modifier = modifier, shape = RoundedCornerShape(12.dp)) { RenderChild(childId) }
    }
}
