// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.components.toggleable.RadioButton
import io.daio.wild.components.toggleable.RadioGroup
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun RadioPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = RadioPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object RadioPageDefaults {
    private val options = listOf("Small", "Medium", "Large")

    @Composable
    private fun radioStyle() =
        StyleDefaults.style(
            colors =
                StyleDefaults.colors(
                    backgroundColor = SiteTheme.colors.surface,
                    contentColor = SiteTheme.colors.textSecondary,
                    selectedBackgroundColor = SiteTheme.colors.accentSubtle,
                    selectedContentColor = SiteTheme.colors.accent,
                    disabledBackgroundColor = SiteTheme.colors.surface,
                    disabledContentColor = SiteTheme.colors.textSecondary,
                ),
        )

    @Composable
    private fun RadioOptions(
        selected: String,
        onSelected: (String) -> Unit,
        enabled: (Int) -> Boolean = { true },
        layout: @Composable (@Composable () -> Unit) -> Unit,
    ) {
        layout {
            options.forEachIndexed { index, option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == option,
                        onClick = { onSelected(option) },
                        enabled = enabled(index),
                        modifier = Modifier.semantics { contentDescription = option },
                        style = radioStyle(),
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = SiteTheme.spacing.s),
                    )
                }
            }
        }
    }

    val data =
        ComponentPageData(
            name = "RadioButton and RadioGroup",
            description =
                "Unstyled, controlled radio primitives. RadioButton delegates interaction, " +
                    "semantics, and a basic default indicator; RadioGroup adds group semantics " +
                    "without owning selection or layout. Callers may replace the indicator artwork.",
            module = "io.daio.wild.components:toggleable",
            demos =
                listOf(
                    Demo("Vertical layout", "Default indicator with caller-owned Column layout.") {
                        var selected by remember { mutableStateOf(options.first()) }
                        RadioGroup {
                            RadioOptions(selected = selected, onSelected = { selected = it }) { content ->
                                Column(verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s)) {
                                    content()
                                }
                            }
                        }
                    },
                    Demo("Horizontal layout", "RadioGroup does not impose Row or Column arrangement.") {
                        var selected by remember { mutableStateOf(options[1]) }
                        RadioGroup {
                            RadioOptions(selected = selected, onSelected = { selected = it }) { content ->
                                Row(horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m)) {
                                    content()
                                }
                            }
                        }
                    },
                    Demo("Disabled", "Disabled items preserve selection semantics and suppress clicks.") {
                        var selected by remember { mutableStateOf(options.first()) }
                        RadioGroup {
                            RadioOptions(
                                selected = selected,
                                onSelected = { selected = it },
                                enabled = { index -> index != 1 },
                            ) { content ->
                                Row(horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m)) {
                                    content()
                                }
                            }
                        }
                    },
                    Demo("Custom indicator", "Replace the default mark with caller artwork.") {
                        var selected by remember { mutableStateOf(options.first()) }
                        RadioGroup {
                            Row(horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m)) {
                                options.forEach { option ->
                                    val isSelected = selected == option
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selected = option },
                                            modifier = Modifier.semantics { contentDescription = option },
                                            style = radioStyle(),
                                        ) {
                                            val color =
                                                if (it) {
                                                    SiteTheme.colors.accent
                                                } else {
                                                    SiteTheme.colors.border
                                                }
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .size(20.dp)
                                                        .background(color, CircleShape),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                if (it) {
                                                    Box(
                                                        modifier =
                                                            Modifier
                                                                .size(8.dp)
                                                                .background(
                                                                    SiteTheme.colors.background,
                                                                    CircleShape,
                                                                ),
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = option,
                                            modifier = Modifier.padding(start = SiteTheme.spacing.s),
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
            usage =
                """
                var selected by remember { mutableStateOf("small") }
                RadioGroup {
                    Column {
                        listOf("small", "medium").forEach { value ->
                            RadioButton(
                                selected = selected == value,
                                onClick = { selected = value },
                            )
                        }
                    }
                }
                // Callers enforce single selection; RadioGroup owns no selection state.

                RadioButton(
                    selected = selected == "small",
                    onClick = { selected = "small" },
                ) { isSelected ->
                    // Replace the default indicator.
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("selected", "Boolean", required = true),
                    Prop("onClick", "() -> Unit", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("style", "Style", default = "RadioButtonDefaults.style()"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop(
                        "indicator (RadioButton)",
                        "@Composable BoxScope.(Boolean) -> Unit",
                        default = "RadioButtonDefaults.Indicator",
                    ),
                    Prop("content (RadioGroup)", "@Composable BoxScope.() -> Unit", required = true),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
