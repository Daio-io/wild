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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.components.toggleable.Checkbox
import io.daio.wild.components.toggleable.TriStateCheckbox
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun CheckboxPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = CheckboxPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object CheckboxPageDefaults {
    private fun nextState(state: ToggleableState): ToggleableState =
        when (state) {
            ToggleableState.Off -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.Off
        }

    @Composable
    private fun checkboxStyle() =
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
            shapes = StyleDefaults.shapes(shape = RoundedCornerShape(6.dp)),
        )

    @Composable
    private fun CheckboxIndicator(
        mark: String,
        color: Color,
    ) {
        Box(
            modifier = Modifier.size(28.dp).background(color, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = mark, color = SiteTheme.colors.textPrimary)
        }
    }

    val data =
        ComponentPageData(
            name = "Checkbox",
            description =
                "Unstyled, controlled Boolean and tri-state checkbox primitives. Wild owns " +
                    "interaction, semantics, and a basic default indicator; callers own " +
                    "tri-state cycling policy and may replace the indicator artwork.",
            module = "io.daio.wild.components:toggleable",
            demos =
                listOf(
                    Demo("States", "Default indicator for Boolean, tri-state, and disabled.") {
                        var checked by remember { mutableStateOf(false) }
                        var state by remember { mutableStateOf(ToggleableState.Indeterminate) }
                        val style = checkboxStyle()

                        Column(verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checked = it },
                                    style = style,
                                )
                                Text(
                                    text = if (checked) "Checked" else "Unchecked",
                                    modifier = Modifier.padding(end = SiteTheme.spacing.m),
                                )
                                TriStateCheckbox(
                                    state = state,
                                    onClick = { state = nextState(state) },
                                    style = style,
                                )
                                Text(text = state.name)
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {},
                                    enabled = false,
                                    style = style,
                                )
                                Text(text = "Disabled")
                            }
                        }
                    },
                    Demo("Custom indicator", "Replace the default mark with caller artwork.") {
                        var checked by remember { mutableStateOf(true) }
                        val style = checkboxStyle()

                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            style = style,
                        ) {
                            CheckboxIndicator(
                                mark = if (it) "✓" else "",
                                color =
                                    if (it) {
                                        SiteTheme.colors.accent
                                    } else {
                                        SiteTheme.colors.surface
                                    },
                            )
                        }
                    },
                ),
            usage =
                """
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                )

                TriStateCheckbox(
                    state = state,
                    onClick = { state = nextState(state) },
                )

                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                ) { isChecked ->
                    // Replace the default indicator.
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("checked (Boolean overload)", "Boolean", required = true),
                    Prop("onCheckedChange (Boolean overload)", "(Boolean) -> Unit", required = true),
                    Prop("state (tri-state overload)", "ToggleableState", required = true),
                    Prop("onClick (tri-state overload)", "() -> Unit", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("style", "Style", default = "CheckboxDefaults.style()"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop(
                        "indicator (Boolean overload)",
                        "@Composable BoxScope.(checked: Boolean) -> Unit",
                        default = "CheckboxDefaults.Indicator",
                    ),
                    Prop(
                        "indicator (tri-state overload)",
                        "@Composable BoxScope.(state: ToggleableState) -> Unit",
                        default = "CheckboxDefaults.Indicator",
                    ),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
