// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui.catalog.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.daio.wild.a2ui.compose.A2uiComponent
import io.daio.wild.a2ui.compose.A2uiComponentProperties
import io.daio.wild.a2ui.compose.A2uiComponentScope
import io.daio.wild.a2ui.compose.A2uiProperty
import io.daio.wild.components.icon.Icon
import io.daio.wild.components.text.Text

/** Renders a known A2UI Icon as an ImageVector, or its name as a text fallback.
 *
 * Example: register [IconComponent] in `wildA2uiBasicCatalogV1()`.
 * @since 0.1.0
 */
object IconComponent : A2uiComponent {
    override val name = "Icon"
    override val properties = listOf(A2uiProperty.string("name", required = true))

    @Composable
    override fun A2uiComponentScope.Content(
        props: A2uiComponentProperties,
        modifier: Modifier,
    ) {
        val name = bindString(props.raw("name")) ?: "unknown"
        knownIcons[name]?.let { Icon(it, contentDescription = name, modifier = modifier) }
            ?: Text(text = name, modifier = modifier)
    }

    private val knownIcons = mapOf("circle" to CircleIcon)
}

private val CircleIcon: ImageVector by lazy {
    ImageVector.Builder("Wild.Circle", 24.dp, 24.dp, 24f, 24f).path {
        moveTo(12f, 2f)
        arcTo(10f, 10f, 0f, true, true, 12f, 22f)
        arcTo(10f, 10f, 0f, true, true, 12f, 2f)
        close()
    }.build()
}
