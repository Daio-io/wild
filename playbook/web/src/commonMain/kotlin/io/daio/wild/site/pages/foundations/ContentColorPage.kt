// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.site.components.CodeBlock
import io.daio.wild.site.components.Prop
import io.daio.wild.site.components.PropsTable
import io.daio.wild.site.theme.SiteTheme

@Composable
fun ContentColorPage(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Content Color",
                style = SiteTheme.typography.h1,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "A CompositionLocal for propagating content color through " +
                        "the composable tree. Child components automatically " +
                        "inherit text and icon colors from parent containers.",
                style = SiteTheme.typography.body,
                color = SiteTheme.colors.textSecondary,
            )
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Live Example
        Text(
            text = "Live Example",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )

        // Demo Card 1: Color Inheritance
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Color Inheritance",
                style = SiteTheme.typography.h3,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "When a parent sets a content color, all nested " +
                        "text and icons automatically inherit that color " +
                        "without needing to set it explicitly.",
                style = SiteTheme.typography.bodySmall,
                color = SiteTheme.colors.textSecondary,
            )
            Container(
                modifier = Modifier.fillMaxWidth(),
                color = SiteTheme.colors.demoBackground,
                shape = RoundedCornerShape(8.dp),
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(16.dp),
                ) {
                    Container(
                        color = Color.White,
                        contentColor = Color.Red,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Content Color: Red",
                                style = SiteTheme.typography.label,
                            )
                            Text(
                                text = "This text is red",
                                style = SiteTheme.typography.bodySmall,
                            )
                        }
                    }
                    Container(
                        color = Color.White,
                        contentColor = Color.Red,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Content Color: Red",
                                style = SiteTheme.typography.label,
                            )
                            Text(
                                text = "Also red, inherited",
                                style = SiteTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }

        // Demo Card 2: Override
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Nested Override",
                style = SiteTheme.typography.h3,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "Child containers can override the inherited " +
                        "content color for their subtree.",
                style = SiteTheme.typography.bodySmall,
                color = SiteTheme.colors.textSecondary,
            )
            Container(
                modifier = Modifier.fillMaxWidth(),
                color = SiteTheme.colors.demoBackground,
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Container(
                        color = Color.White,
                        contentColor = Color.Red,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Parent Content Color: Red",
                                style = SiteTheme.typography.label,
                            )
                            Text(
                                text = "This text is red",
                                style = SiteTheme.typography.bodySmall,
                            )
                            Container(
                                color = Color(0xFFF0F0F0),
                                contentColor = Color.Blue,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text =
                                            "Child Content Color: Blue",
                                        style =
                                            SiteTheme.typography.label,
                                    )
                                    Text(
                                        text = "Overridden from red",
                                        style =
                                            SiteTheme.typography.bodySmall,
                                    )
                                }
                            }
                            Text(
                                text = "Back to red (sibling)",
                                style = SiteTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Dependency
        Text(
            text = "Dependency",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        CodeBlock(
            code = "implementation(\"io.daio.wild:content-color:<version>\")",
            tabs = listOf("build.gradle.kts"),
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // Usage
        Text(
            text = "Usage",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        CodeBlock(
            code = USAGE_CODE,
            tabs = listOf("Kotlin"),
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // API Reference
        Text(
            text = "API Reference",
            style = SiteTheme.typography.h2,
            color = SiteTheme.colors.textPrimary,
        )
        PropsTable(
            props =
                listOf(
                    Prop("ProvidesContentColor()", "@Composable"),
                    Prop("LocalContentColor", "CompositionLocal"),
                    Prop("color", "Color", required = true),
                    Prop("content", "@Composable () -> Unit", required = true),
                ),
        )
    }
}

private val USAGE_CODE =
    """
    ProvidesContentColor(color = Color.White) {
        // All children inherit white content color
        Text("Inherits white")  // no explicit color needed

        ProvidesContentColor(color = Color.Gray) {
            // Nested override
            Text("Inherits gray")
        }
    }

    // Read current content color
    val currentColor = LocalContentColor.current
    """.trimIndent()
