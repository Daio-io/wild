// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.site.components.CodeBlock
import io.daio.wild.site.components.Prop
import io.daio.wild.site.components.PropsTable
import io.daio.wild.site.theme.SiteTheme

@Composable
fun InteractionStatePage(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Interaction State",
            style = SiteTheme.typography.h1,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text =
                "InteractionState tracks whether a component is focused, hovered, pressed, selected, or enabled. " +
                    "The Style system reads these states to apply the correct visual properties.",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        Text(
            text = "How It Works",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text =
                "Interactive Wild components (Container, Button, ListItem, Toggleable) automatically track " +
                    "interaction state via MutableInteractionSource. The Style system observes these states and " +
                    "applies the matching visual properties (colors, borders, scale, alpha).",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )
        CodeBlock(
            code =
                """
                // The Style system maps states automatically:
                val style = StyleDefaults.style(
                    colors = StyleDefaults.colors(
                        backgroundColor = Color.White,
                        hoveredBackgroundColor = Color.LightGray,
                        focusedBackgroundColor = Color.Cyan,
                        pressedBackgroundColor = Color.Gray,
                        selectedBackgroundColor = Color.Blue,
                    ),
                )

                // Pass to any interactive component
                Container(
                    onClick = { },
                    style = style,
                ) {
                    // Content color also changes per state
                    Text("Hover, focus, or click me")
                }
                """.trimIndent(),
            tabs = listOf("Kotlin"),
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        Text(
            text = "InteractionState Interface",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        PropsTable(
            props =
                listOf(
                    Prop("focused", "Boolean", default = "false"),
                    Prop("hovered", "Boolean", default = "false"),
                    Prop("pressed", "Boolean", default = "false"),
                    Prop("selected", "Boolean", default = "false"),
                    Prop("enabled", "Boolean", default = "true"),
                ),
        )
    }
}
