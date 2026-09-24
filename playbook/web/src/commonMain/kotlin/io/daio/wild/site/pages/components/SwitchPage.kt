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
import io.daio.wild.components.switch.Switch
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun SwitchPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = SwitchPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object SwitchPageDefaults {
    @Composable
    private fun switchStyle() =
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
            shapes = StyleDefaults.shapes(shape = RoundedCornerShape(16.dp)),
        )

    @Composable
    private fun TrackAndThumb(checked: Boolean) {
        val trackColor =
            if (checked) {
                SiteTheme.colors.accent
            } else {
                SiteTheme.colors.border
            }
        val thumbColor = if (checked) SiteTheme.colors.background else SiteTheme.colors.surface

        Box(
            modifier = Modifier.size(width = 52.dp, height = 32.dp).background(trackColor, CircleShape),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier.padding(4.dp).size(24.dp).background(thumbColor, CircleShape),
            )
        }
    }

    val data =
        ComponentPageData(
            name = "Switch",
            description =
                "An unstyled, controlled binary switch primitive. Wild owns click interaction " +
                    "and switch semantics; callers own the track, thumb, dimensions, and artwork.",
            module = "io.daio.wild.components:switch",
            demos =
                listOf(
                    Demo("On and off", "The caller controls the Boolean state and renders both states.") {
                        var checked by remember { mutableStateOf(false) }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Switch(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                style = switchStyle(),
                            ) { TrackAndThumb(checked = it) }
                            Text(text = if (checked) "On" else "Off")
                        }
                    },
                    Demo("Disabled and rejected", "Disabled switches suppress clicks; callers may also reject a proposal.") {
                        Column(verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Switch(
                                    checked = true,
                                    onCheckedChange = {},
                                    enabled = false,
                                    style = switchStyle(),
                                ) { TrackAndThumb(checked = it) }
                                Text(text = "Disabled")
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Switch(
                                    checked = true,
                                    onCheckedChange = {},
                                    style = switchStyle(),
                                ) { TrackAndThumb(checked = it) }
                                Text(text = "Callback rejected")
                            }
                        }
                    },
                    Demo("Caller-rendered track and thumb", "The primitive provides the slot; all visual policy stays with the caller.") {
                        var checked by remember { mutableStateOf(true) }

                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            style = switchStyle(),
                        ) {
                            TrackAndThumb(checked = checked)
                        }
                    },
                ),
            usage =
                """
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                ) { isChecked ->
                    // Render the track and thumb for isChecked.
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("checked", "Boolean", required = true),
                    Prop("onCheckedChange", "(Boolean) -> Unit", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("style", "Style", default = "SwitchDefaults.style()"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop(
                        "content",
                        "@Composable BoxScope.(checked: Boolean) -> Unit",
                        required = true,
                    ),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
