// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.button.Button
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.components.CodeBlock
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.PlatformBadges
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults

@Composable
fun GettingStartedPage(
    onNavigateToComponents: () -> Unit,
    onNavigateToFoundations: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(SiteTheme.spacing.s)
    val cardBorder = Border(width = 1.dp, color = SiteTheme.colors.border, shape = cardShape)
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SiteTheme.spacing.xxl, vertical = SiteTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.xl),
    ) {
        // Hero Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            Text(
                text = "Getting Started",
                style = SiteTheme.typography.h1,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "Learn how to install and use Wild primitives in your " +
                        "Compose Multiplatform project.",
                style = SiteTheme.typography.body,
                color = SiteTheme.colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // What is Wild? Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            Text(
                text = "What is Wild?",
                style = SiteTheme.typography.h2,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "Wild is an unstyled, accessible primitives library for " +
                        "Compose Multiplatform. It provides foundational building " +
                        "blocks — Button, Container, Text, Icon, ListItem, " +
                        "Toggleable, and Divider — plus utilities like Style, " +
                        "Content Color, and Modifier. These primitives give you " +
                        "full control over styling while handling accessibility " +
                        "and interaction patterns out of the box.",
                style = SiteTheme.typography.body,
                color = SiteTheme.colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Installation Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            Text(
                text = "Installation",
                style = SiteTheme.typography.h2,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text =
                    "Add the dependencies you need to your module's " +
                        "build.gradle.kts file:",
                style = SiteTheme.typography.body,
                color = SiteTheme.colors.textSecondary,
            )
            CodeBlock(
                code = INSTALL_GRADLE,
                tabs = listOf("build.gradle.kts", "libs.versions.toml"),
                tabContents =
                    mapOf(
                        "build.gradle.kts" to INSTALL_GRADLE,
                        "libs.versions.toml" to INSTALL_TOML,
                    ),
            )
        }

        // Quick Example Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            Text(
                text = "Quick Example",
                style = SiteTheme.typography.h2,
                color = SiteTheme.colors.textPrimary,
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
            ) {
                CodeBlock(
                    modifier = Modifier.weight(1f),
                    code =
                        """
                        @Composable
                        fun MyThemedButton() {
                            Button(
                                onClick = { /* handle */ },
                                style = StyleDefaults.style(
                                    colors = StyleDefaults.colors(
                                        backgroundColor = SageGreen,
                                    )
                                )
                            ) { Text("Click me") }
                        }
                        """.trimIndent(),
                    tabs = listOf("Kotlin"),
                )
                Container(
                    modifier = Modifier.width(280.dp).fillMaxHeight(),
                    color = SiteTheme.colors.surface,
                    shape = cardShape,
                    border = cardBorder,
                ) {
                    Column(
                        modifier = Modifier.padding(SiteTheme.spacing.m).fillMaxWidth(),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                SiteTheme.spacing.m,
                                Alignment.CenterVertically,
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "PREVIEW",
                            style = SiteTheme.typography.label,
                            color = SiteTheme.colors.textSecondary,
                        )
                        Button(
                            onClick = {},
                            style =
                                StyleDefaults.style(
                                    colors =
                                        StyleDefaults.colors(
                                            backgroundColor = SiteTheme.colors.accent,
                                            contentColor = SiteTheme.colors.background,
                                            hoveredBackgroundColor =
                                                SiteTheme.colors.accent.copy(alpha = 0.8f),
                                            pressedBackgroundColor =
                                                SiteTheme.colors.accent.copy(alpha = 0.6f),
                                        ),
                                    shapes =
                                        StyleDefaults.shapes(
                                            shape = RoundedCornerShape(SiteTheme.spacing.s),
                                        ),
                                    scale =
                                        StyleDefaults.scale(
                                            pressedScale = 0.95f,
                                        ),
                                ),
                        ) {
                            Text(
                                text = "Click me",
                                modifier =
                                    Modifier.padding(
                                        horizontal = SiteTheme.spacing.l,
                                        vertical = SiteTheme.spacing.s,
                                    ),
                            )
                        }
                    }
                }
            }
        }

        // Platform Availability Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            Text(
                text = "Platform Availability",
                style = SiteTheme.typography.h2,
                color = SiteTheme.colors.textPrimary,
            )
            PlatformBadges(
                platforms =
                    listOf(
                        Platform.Android,
                        Platform.AndroidTV,
                        Platform.Desktop,
                        Platform.MacOS,
                        Platform.Web,
                        Platform.IOS,
                    ),
            )
        }

        // Navigation Link Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
        ) {
            Container(
                onClick = onNavigateToComponents,
                modifier = Modifier.weight(1f),
                style =
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                backgroundColor = SiteTheme.colors.surface,
                                hoveredBackgroundColor =
                                    SiteTheme.colors.surface.copy(alpha = 0.8f),
                                pressedBackgroundColor =
                                    SiteTheme.colors.surface.copy(alpha = 0.6f),
                            ),
                        shapes = StyleDefaults.shapes(shape = cardShape),
                        borders =
                            StyleDefaults.borders(
                                border = cardBorder,
                                hoveredBorder =
                                    Border(
                                        width = 1.dp,
                                        color = SiteTheme.colors.accent,
                                        shape = cardShape,
                                    ),
                            ),
                        scale = StyleDefaults.scale(pressedScale = 0.98f),
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(SiteTheme.spacing.m).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
                ) {
                    Text(
                        text = "Components",
                        style = SiteTheme.typography.h3,
                        color = SiteTheme.colors.accent,
                    )
                    Text(
                        text =
                            "Explore unstyled, accessible UI primitives " +
                                "like Button, Container, and ListItem.",
                        style = SiteTheme.typography.bodySmall,
                        color = SiteTheme.colors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Container(
                onClick = onNavigateToFoundations,
                modifier = Modifier.weight(1f),
                style =
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                backgroundColor = SiteTheme.colors.surface,
                                hoveredBackgroundColor =
                                    SiteTheme.colors.surface.copy(alpha = 0.8f),
                                pressedBackgroundColor =
                                    SiteTheme.colors.surface.copy(alpha = 0.6f),
                            ),
                        shapes = StyleDefaults.shapes(shape = cardShape),
                        borders =
                            StyleDefaults.borders(
                                border = cardBorder,
                                hoveredBorder =
                                    Border(
                                        width = 1.dp,
                                        color = SiteTheme.colors.accent,
                                        shape = cardShape,
                                    ),
                            ),
                        scale = StyleDefaults.scale(pressedScale = 0.98f),
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(SiteTheme.spacing.m).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
                ) {
                    Text(
                        text = "Foundations",
                        style = SiteTheme.typography.h3,
                        color = SiteTheme.colors.accent,
                    )
                    Text(
                        text =
                            "Learn about the Style system, Content Color, " +
                                "and Modifier utilities.",
                        style = SiteTheme.typography.bodySmall,
                        color = SiteTheme.colors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

private val INSTALL_GRADLE =
    """
    dependencies {
        // Core utilities and helpers
        implementation("io.daio.wild:foundations:<version>")
        // State-based styling (colors, shapes, borders, scale)
        implementation("io.daio.wild:style:<version>")
        // Content color propagation via CompositionLocal
        implementation("io.daio.wild:content-color:<version>")
        // Conditional modifier extensions (thenIf, thenIfNotNull)
        implementation("io.daio.wild:modifier:<version>")
        // Container layout primitive
        implementation("io.daio.wild:container:<version>")
        // Interactive button primitive
        implementation("io.daio.wild:button:<version>")
    }
    """.trimIndent()

private val INSTALL_TOML =
    """
    [versions]
    wild = "<version>"

    [libraries]
    # Core utilities and helpers
    wild-foundations = { module = "io.daio.wild:foundations", version.ref = "wild" }
    # State-based styling (colors, shapes, borders, scale)
    wild-style = { module = "io.daio.wild:style", version.ref = "wild" }
    # Content color propagation via CompositionLocal
    wild-content-color = { module = "io.daio.wild:content-color", version.ref = "wild" }
    # Conditional modifier extensions (thenIf, thenIfNotNull)
    wild-modifier = { module = "io.daio.wild:modifier", version.ref = "wild" }
    # Container layout primitive
    wild-container = { module = "io.daio.wild:container", version.ref = "wild" }
    # Interactive button primitive
    wild-button = { module = "io.daio.wild:button", version.ref = "wild" }
    """.trimIndent()
