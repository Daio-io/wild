// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.components.toggleable.Selectable
import io.daio.wild.components.toggleable.Toggleable
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun ToggleablePage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = ToggleablePageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object ToggleablePageDefaults {
    private val selectableLabels = listOf("Small", "Medium", "Large")

    val data =
        ComponentPageData(
            name = "Toggleable",
            description =
                "Primitives for building checkboxes, switches, and radio buttons. " +
                    "Toggleable manages its own boolean state; Selectable defers to parent-managed single selection.",
            module = "io.daio.wild:toggleable",
            demos =
                listOf(
                    Demo("Toggleable", "Click to toggle checked state.") {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            var checked1 by remember { mutableStateOf(false) }
                            var checked2 by remember { mutableStateOf(true) }

                            Toggleable(
                                checked = checked1,
                                onCheckedChange = { checked1 = it },
                                modifier = Modifier.size(56.dp),
                                style =
                                    StyleDefaults.style(
                                        colors =
                                            StyleDefaults.colors(
                                                backgroundColor = SiteTheme.colors.surface,
                                                contentColor = SiteTheme.colors.textPrimary,
                                                selectedBackgroundColor = SiteTheme.colors.accent,
                                                selectedContentColor = SiteTheme.colors.background,
                                                focusedSelectedBackgroundColor = SiteTheme.colors.accent,
                                                focusedSelectedContentColor = SiteTheme.colors.background,
                                                hoveredSelectedBackgroundColor = SiteTheme.colors.accent,
                                                hoveredSelectedContentColor = SiteTheme.colors.background,
                                            ),
                                        shapes = StyleDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                                    ),
                            ) {}
                            Toggleable(
                                checked = checked2,
                                onCheckedChange = { checked2 = it },
                                modifier = Modifier.size(56.dp),
                                style =
                                    StyleDefaults.style(
                                        colors =
                                            StyleDefaults.colors(
                                                backgroundColor = SiteTheme.colors.surface,
                                                contentColor = SiteTheme.colors.textPrimary,
                                                selectedBackgroundColor = SiteTheme.colors.accent,
                                                selectedContentColor = SiteTheme.colors.background,
                                                focusedSelectedBackgroundColor = SiteTheme.colors.accent,
                                                focusedSelectedContentColor = SiteTheme.colors.background,
                                                hoveredSelectedBackgroundColor = SiteTheme.colors.accent,
                                                hoveredSelectedContentColor = SiteTheme.colors.background,
                                            ),
                                        shapes = StyleDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                                    ),
                            ) {}
                        }
                    },
                    Demo("Selectable", "Radio-button style - only one selected at a time.") {
                        var selected by remember { mutableStateOf(0) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectableLabels.forEachIndexed { index, label ->
                                Selectable(
                                    selected = selected == index,
                                    onClick = { selected = index },
                                    style =
                                        StyleDefaults.style(
                                            colors =
                                                StyleDefaults.colors(
                                                    backgroundColor = SiteTheme.colors.surface,
                                                    contentColor = SiteTheme.colors.textSecondary,
                                                    selectedBackgroundColor = SiteTheme.colors.accentSubtle,
                                                    selectedContentColor = SiteTheme.colors.accent,
                                                    focusedSelectedBackgroundColor = SiteTheme.colors.accentSubtle,
                                                    focusedSelectedContentColor = SiteTheme.colors.accent,
                                                    hoveredSelectedBackgroundColor = SiteTheme.colors.accentSubtle,
                                                    hoveredSelectedContentColor = SiteTheme.colors.accent,
                                                ),
                                            shapes = StyleDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                                        ),
                                ) {
                                    Text(
                                        text = label,
                                        modifier =
                                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                                .align(Alignment.Center),
                                    )
                                }
                            }
                        }
                    },
                ),
            usage =
                """
                // Toggleable (checkbox-style)
                var checked by remember { mutableStateOf(false) }
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                ) {
                    Text(if (checked) "+" else "")
                }

                // Selectable (radio-style)
                Selectable(
                    selected = isSelected,
                    onClick = { onSelect() },
                ) {
                    Text("Option")
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("checked", "Boolean", required = true),
                    Prop("onCheckedChange", "(Boolean) -> Unit", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("style", "Style", default = "ToggleableDefaults.style()"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop("content", "@Composable BoxScope.() -> Unit", required = true),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
