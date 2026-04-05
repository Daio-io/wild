// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StylePage(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Style",
            style = SiteTheme.typography.h1,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text =
                "The Style system defines how components look across different " +
                    "interaction states: default, hovered, focused, pressed, and " +
                    "selected. Compose styles from colors, shapes, borders, scale, " +
                    "and alpha.",
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // Colors Demo
        SectionHeader("Colors")
        SectionDescription(
            "Background and content colors change per interaction state. " +
                "Hover and click each box to see the transitions.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Hover Color",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        ),
                )
                DemoBox(
                    label = "Press Color",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    pressedBackgroundColor = Color(0xFF4ADE80),
                                    pressedContentColor = Color.Black,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        ),
                )
                DemoBox(
                    label = "Hover + Press",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                    pressedBackgroundColor =
                                        SiteTheme.colors.accent.copy(alpha = 0.6f),
                                    pressedContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        ),
                )
                DemoBox(
                    label = "Disabled",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    disabledBackgroundColor =
                                        SiteTheme.colors.surface,
                                    disabledContentColor =
                                        SiteTheme.colors.textSecondary.copy(alpha = 0.5f),
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        ),
                    enabled = false,
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Scale Demo
        SectionHeader("Scale")
        SectionDescription(
            "Scale transforms grow or shrink components on interaction. " +
                "Combine with colors for richer feedback.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Hover Grow",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            scale = StyleDefaults.scale(hoveredScale = 1.1f),
                        ),
                )
                DemoBox(
                    label = "Press Shrink",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            scale = StyleDefaults.scale(pressedScale = 0.9f),
                        ),
                )
                DemoBox(
                    label = "Grow + Shrink",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            scale =
                                StyleDefaults.scale(
                                    hoveredScale = 1.05f,
                                    pressedScale = 0.95f,
                                ),
                        ),
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Borders Demo
        SectionHeader("Borders")
        SectionDescription(
            "Borders can appear, change color, or change width per state. " +
                "Useful for focus rings and selection indicators.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Hover Border",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    hoveredBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
                DemoBox(
                    label = "Press Border",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    pressedBorder =
                                        Border(
                                            width = 2.dp,
                                            color = Color(0xFF4ADE80),
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
                DemoBox(
                    label = "Default + Hover",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    hoveredBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Shapes Demo
        SectionHeader("Shapes")
        SectionDescription(
            "Shape transforms change the corner radius or clip path per state.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Square",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(0.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(0.dp),
                                        ),
                                ),
                        ),
                )
                DemoBox(
                    label = "Rounded",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(16.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(16.dp),
                                        ),
                                ),
                        ),
                )
                DemoBox(
                    label = "Circle",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(shape = CircleShape),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = CircleShape,
                                        ),
                                ),
                        ),
                )
                DemoBox(
                    label = "Morph on Hover",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                    hoveredShape = CircleShape,
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Alpha Demo
        SectionHeader("Alpha")
        SectionDescription(
            "Alpha controls overall opacity per interaction state. " +
                "Commonly used for disabled states.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Hover Fade",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.accent,
                                    contentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            alpha = StyleDefaults.alpha(hoveredAlpha = 0.6f),
                        ),
                )
                DemoBox(
                    label = "Press Fade",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.accent,
                                    contentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            alpha = StyleDefaults.alpha(pressedAlpha = 0.4f),
                        ),
                )
                DemoBox(
                    label = "Disabled 50%",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.accent,
                                    contentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            alpha = StyleDefaults.alpha(disabledAlpha = 0.5f),
                        ),
                    enabled = false,
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Selected Demo
        SectionHeader("Selected")
        SectionDescription(
            "Selected states highlight active or toggled elements. " +
                "Each pair shows the default and selected state side by side.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SelectableDemoBox(
                    label = "Selected Color",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.background,
                                    focusedBackgroundColor = SiteTheme.colors.background,
                                    pressedBackgroundColor = SiteTheme.colors.background,
                                    selectedBackgroundColor = SiteTheme.colors.accent,
                                    selectedContentColor = SiteTheme.colors.background,
                                    hoveredSelectedBackgroundColor = SiteTheme.colors.accent,
                                    focusedSelectedBackgroundColor = SiteTheme.colors.accent,
                                    pressedSelectedBackgroundColor = SiteTheme.colors.accent,
                                    hoveredSelectedContentColor = SiteTheme.colors.background,
                                    focusedSelectedContentColor = SiteTheme.colors.background,
                                    pressedSelectedContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
                SelectableDemoBox(
                    label = "Selected Border",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.background,
                                    focusedBackgroundColor = SiteTheme.colors.background,
                                    selectedBackgroundColor =
                                        SiteTheme.colors.accent.copy(alpha = 0.1f),
                                    selectedContentColor = SiteTheme.colors.accent,
                                    hoveredSelectedBackgroundColor =
                                        SiteTheme.colors.accent.copy(alpha = 0.1f),
                                    focusedSelectedBackgroundColor =
                                        SiteTheme.colors.accent.copy(alpha = 0.1f),
                                    hoveredSelectedContentColor = SiteTheme.colors.accent,
                                    focusedSelectedContentColor = SiteTheme.colors.accent,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    hoveredBorder =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    focusedBorder =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    selectedBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    hoveredSelectedBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    focusedSelectedBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                        ),
                )
                SelectableDemoBox(
                    label = "Selected Scale",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.background,
                                    focusedBackgroundColor = SiteTheme.colors.background,
                                    selectedBackgroundColor = SiteTheme.colors.accent,
                                    selectedContentColor = SiteTheme.colors.background,
                                    hoveredSelectedBackgroundColor = SiteTheme.colors.accent,
                                    focusedSelectedBackgroundColor = SiteTheme.colors.accent,
                                    hoveredSelectedContentColor = SiteTheme.colors.background,
                                    focusedSelectedContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                            scale =
                                StyleDefaults.scale(
                                    hoveredScale = 1f,
                                    focusedScale = 1f,
                                    selectedScale = 1.1f,
                                    hoveredSelectedScale = 1.1f,
                                    focusedSelectedScale = 1.1f,
                                ),
                        ),
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Combined Demo
        SectionHeader("Combined")
        SectionDescription(
            "All style properties compose together. Combine colors, " +
                "borders, scale, shapes, and alpha for rich interactions.",
        )
        DemoContainer {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DemoBox(
                    label = "Full Style",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = SiteTheme.colors.background,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor = SiteTheme.colors.accent,
                                    hoveredContentColor = SiteTheme.colors.background,
                                    pressedBackgroundColor =
                                        SiteTheme.colors.accent.copy(alpha = 0.7f),
                                    pressedContentColor = SiteTheme.colors.background,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    hoveredBorder =
                                        Border(
                                            width = 2.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                            scale =
                                StyleDefaults.scale(
                                    hoveredScale = 1.05f,
                                    pressedScale = 0.95f,
                                ),
                        ),
                )
                DemoBox(
                    label = "Pill Button",
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
                                StyleDefaults.shapes(shape = CircleShape),
                            scale =
                                StyleDefaults.scale(
                                    hoveredScale = 1.05f,
                                    pressedScale = 0.9f,
                                ),
                        ),
                )
                DemoBox(
                    label = "Ghost Style",
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    hoveredBackgroundColor =
                                        SiteTheme.colors.surface,
                                    hoveredContentColor = SiteTheme.colors.accent,
                                ),
                            shapes =
                                StyleDefaults.shapes(
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            borders =
                                StyleDefaults.borders(
                                    border =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.border,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    hoveredBorder =
                                        Border(
                                            width = 1.dp,
                                            color = SiteTheme.colors.accent,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                ),
                            scale = StyleDefaults.scale(pressedScale = 0.95f),
                        ),
                )
            }
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Usage
        SectionHeader("Usage")
        CodeBlock(
            code = STYLE_USAGE,
            tabs = listOf("Kotlin"),
        )

        HorizontalDivider(color = SiteTheme.colors.border)

        // API Reference
        SectionHeader("Style Properties")
        PropsTable(
            props =
                listOf(
                    Prop("colors", "Colors", required = true),
                    Prop("borders", "Borders", required = true),
                    Prop("scale", "Scale", required = true),
                    Prop("shapes", "Shapes", required = true),
                    Prop("alpha", "Alpha", required = true),
                ),
        )

        SectionHeader("Colors")
        PropsTable(
            props =
                listOf(
                    Prop("backgroundColor", "Color", required = true),
                    Prop("contentColor", "Color", required = true),
                    Prop("focusedBackgroundColor", "Color"),
                    Prop("hoveredBackgroundColor", "Color"),
                    Prop("pressedBackgroundColor", "Color"),
                    Prop("selectedBackgroundColor", "Color"),
                    Prop("disabledBackgroundColor", "Color"),
                    Prop("focusedContentColor", "Color"),
                    Prop("hoveredContentColor", "Color"),
                    Prop("pressedContentColor", "Color"),
                    Prop("selectedContentColor", "Color"),
                    Prop("disabledContentColor", "Color"),
                ),
        )

        SectionHeader("Scale")
        PropsTable(
            props =
                listOf(
                    Prop("scale", "Float", default = "1f"),
                    Prop("focusedScale", "Float"),
                    Prop("hoveredScale", "Float"),
                    Prop("pressedScale", "Float"),
                    Prop("selectedScale", "Float"),
                    Prop("disabledScale", "Float"),
                ),
        )

        SectionHeader("Borders")
        PropsTable(
            props =
                listOf(
                    Prop("border", "Border"),
                    Prop("focusedBorder", "Border"),
                    Prop("hoveredBorder", "Border"),
                    Prop("pressedBorder", "Border"),
                    Prop("selectedBorder", "Border"),
                    Prop("disabledBorder", "Border"),
                ),
        )

        SectionHeader("Border")
        PropsTable(
            props =
                listOf(
                    Prop("width", "Dp", required = true),
                    Prop("color", "Color", required = true),
                    Prop("shape", "Shape", required = true),
                    Prop("inset", "Dp", default = "0.dp"),
                ),
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = SiteTheme.typography.h2,
        color = SiteTheme.colors.textPrimary,
    )
}

@Composable
private fun SectionDescription(text: String) {
    Text(
        text = text,
        style = SiteTheme.typography.body,
        color = SiteTheme.colors.textSecondary,
    )
}

@Composable
private fun DemoContainer(content: @Composable () -> Unit) {
    Container(
        modifier = Modifier.fillMaxWidth(),
        color = SiteTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}

@Composable
private fun DemoBox(
    label: String,
    style: io.daio.wild.style.Style,
    enabled: Boolean = true,
) {
    Container(
        onClick = {},
        enabled = enabled,
        modifier = Modifier.size(120.dp),
        style = style,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = SiteTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SelectableDemoBox(
    label: String,
    style: io.daio.wild.style.Style,
) {
    var selected by remember { mutableStateOf(false) }
    Container(
        onClick = { selected = !selected },
        selected = selected,
        modifier = Modifier.size(120.dp),
        style = style,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    style = SiteTheme.typography.bodySmall,
                )
                Text(
                    text = if (selected) "Selected" else "Click me",
                    style = SiteTheme.typography.label,
                )
            }
        }
    }
}

private val STYLE_USAGE =
    """
    val myStyle = StyleDefaults.style(
        colors = StyleDefaults.colors(
            backgroundColor = Color.White,
            contentColor = Color.Black,
            hoveredBackgroundColor = Color.LightGray,
            pressedBackgroundColor = Color.Gray,
        ),
        shapes = StyleDefaults.shapes(
            shape = RoundedCornerShape(8.dp),
        ),
        scale = StyleDefaults.scale(
            hoveredScale = 1.02f,
            pressedScale = 0.98f,
        ),
        borders = StyleDefaults.borders(
            focusedBorder = Border(
                color = Color.Blue,
                width = 2.dp,
                shape = RoundedCornerShape(8.dp),
            ),
        ),
        alpha = StyleDefaults.alpha(
            disabledAlpha = 0.5f,
        ),
    )

    // Apply to any interactive component
    Container(onClick = { }, style = myStyle) {
        Text("Styled container")
    }
    """.trimIndent()
