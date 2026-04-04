// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.daio.wild.components.listitem.ListItem
import io.daio.wild.components.text.Text
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun ListItemPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = ListItemPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object ListItemPageDefaults {
    private val basicLabels = listOf("Home", "Favorites", "Settings")
    private val leadingTrailingItems =
        listOf(
            Pair("Home", "H"),
            Pair("Favorites", "F"),
            Pair("Settings", "S"),
        )

    val data =
        ComponentPageData(
            name = "ListItem",
            description =
                "A list item with optional leading and trailing content slots. " +
                    "Supports click, selection, and state-based styling.",
            module = "io.daio.wild.components:list-item",
            demos =
                listOf(
                    Demo("Basic", "Simple list items.") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            basicLabels.forEach { label ->
                                ListItem(
                                    onClick = {},
                                    style =
                                        StyleDefaults.style(
                                            colors =
                                                StyleDefaults.colors(
                                                    backgroundColor = SiteTheme.colors.surface,
                                                    contentColor = SiteTheme.colors.textPrimary,
                                                    hoveredBackgroundColor = SiteTheme.colors.accentSubtle,
                                                ),
                                            shapes = StyleDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                                        ),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    },
                    Demo("Leading and Trailing", "List items with leading avatar and trailing indicator.") {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            leadingTrailingItems.forEach { (label, initial) ->
                                ListItem(
                                    onClick = {},
                                    leadingContent = {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(SiteTheme.colors.accentSubtle),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = initial,
                                                style = SiteTheme.typography.label,
                                                color = SiteTheme.colors.accent,
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        Text(
                                            text = ">",
                                            color = SiteTheme.colors.textSecondary,
                                        )
                                    },
                                    style =
                                        StyleDefaults.style(
                                            colors =
                                                StyleDefaults.colors(
                                                    backgroundColor = SiteTheme.colors.surface,
                                                    contentColor = SiteTheme.colors.textPrimary,
                                                    hoveredBackgroundColor = SiteTheme.colors.accentSubtle,
                                                ),
                                            shapes = StyleDefaults.shapes(shape = RoundedCornerShape(8.dp)),
                                        ),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    },
                ),
            usage =
                """
                ListItem(
                    onClick = { /* handle click */ },
                    leadingContent = {
                        Text("*")
                    },
                    trailingContent = {
                        Text(">")
                    },
                ) {
                    Text("Home")
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("onClick", "() -> Unit", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("selected", "Boolean", default = "false"),
                    Prop("leadingContent", "(@Composable () -> Unit)?"),
                    Prop("trailingContent", "(@Composable () -> Unit)?"),
                    Prop("style", "Style", default = "ListItemDefaults.style()"),
                    Prop("contentPadding", "PaddingValues", default = "ListItemDefaults.contentPadding"),
                    Prop("verticalAlignment", "Alignment.Vertical", default = "CenterVertically"),
                    Prop("horizontalArrangement", "Arrangement.Horizontal", default = "spacedBy(16.dp)"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop("content", "@Composable () -> Unit", required = true),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}
