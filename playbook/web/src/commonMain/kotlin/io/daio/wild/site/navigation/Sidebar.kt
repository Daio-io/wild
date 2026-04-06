// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.components.Chevron
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

data class SidebarGroup(
    val title: String,
    val items: List<SidebarItem>,
)

data class SidebarItem(
    val label: String,
    val route: Route,
)

@Composable
fun Sidebar(
    groups: List<SidebarGroup>,
    currentRoute: Route,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(220.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = SiteTheme.spacing.m, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.xs),
    ) {
        groups.forEach { group ->
            SidebarGroupSection(
                group = group,
                currentRoute = currentRoute,
                onRouteSelected = onRouteSelected,
            )
        }
    }
}

@Composable
private fun SidebarGroupSection(
    group: SidebarGroup,
    currentRoute: Route,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxWidth().padding(bottom = SiteTheme.spacing.s)) {
        Container(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().height(32.dp),
            style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = SiteTheme.colors.background,
                            contentColor = SiteTheme.colors.textSecondary,
                        ),
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = SiteTheme.spacing.s),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = group.title.uppercase(),
                    style = SiteTheme.typography.label,
                )
                Chevron(
                    expanded = expanded,
                    color = SiteTheme.colors.textSecondary,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                group.items.forEach { item ->
                    SidebarNavItem(
                        item = item,
                        selected = item.route == currentRoute,
                        onClick = { onRouteSelected(item.route) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    item: SidebarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        if (selected) {
            StyleDefaults.colors(
                backgroundColor = SiteTheme.colors.accentSubtle,
                contentColor = SiteTheme.colors.accent,
            )
        } else {
            StyleDefaults.colors(
                backgroundColor = SiteTheme.colors.background,
                contentColor = SiteTheme.colors.textPrimary,
                hoveredBackgroundColor = SiteTheme.colors.surface,
            )
        }

    Container(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(36.dp),
        style =
            StyleDefaults.style(
                colors = colors,
                shapes = StyleDefaults.shapes(shape = RoundedCornerShape(6.dp)),
            ),
    ) {
        // Use Box with contentAlignment to center text vertically.
        // Box (propagateMinConstraints=false by default) doesn't force min constraints
        // onto Text, so Text stays natural-sized and contentAlignment positions it.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = item.label,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = SiteTheme.typography.body,
            )
        }
    }
}

fun sidebarGroupsForSection(section: Section): List<SidebarGroup> =
    when (section) {
        Section.GettingStarted -> emptyList()
        Section.Components ->
            listOf(
                SidebarGroup(
                    title = "Interactive",
                    items =
                        listOf(
                            SidebarItem("Button", Route.Component.Button),
                            SidebarItem("Toggleable", Route.Component.Toggleable),
                        ),
                ),
                SidebarGroup(
                    title = "Display",
                    items =
                        listOf(
                            SidebarItem("Text", Route.Component.Text),
                            SidebarItem("Icon", Route.Component.Icon),
                        ),
                ),
                SidebarGroup(
                    title = "Layout",
                    items =
                        listOf(
                            SidebarItem("Container", Route.Component.Container),
                            SidebarItem("ListItem", Route.Component.ListItem),
                            SidebarItem("Divider", Route.Component.Divider),
                        ),
                ),
            )
        Section.Foundations ->
            listOf(
                SidebarGroup(
                    title = "Core",
                    items =
                        listOf(
                            SidebarItem("Style", Route.Foundation.Style),
                            SidebarItem("Content Color", Route.Foundation.ContentColor),
                            SidebarItem("Modifier", Route.Foundation.Modifier),
                            SidebarItem("Interaction State", Route.Foundation.InteractionState),
                        ),
                ),
            )
    }
