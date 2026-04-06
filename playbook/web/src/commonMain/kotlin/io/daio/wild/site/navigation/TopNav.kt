// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.WildLogo
import io.daio.wild.site.components.SearchBar
import io.daio.wild.site.components.SearchResult
import io.daio.wild.site.components.ThemeToggle
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun TopNav(
    currentSection: Section,
    isDarkTheme: Boolean,
    onSectionSelected: (Section) -> Unit,
    onToggleTheme: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    Container(
        modifier = modifier.fillMaxWidth().height(56.dp),
        color = SiteTheme.colors.surface,
        contentColor = SiteTheme.colors.textPrimary,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = SiteTheme.spacing.l),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        ) {
            WildLogo()

            Text(
                text = "Wild",
                style =
                    SiteTheme.typography.h3.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                color = SiteTheme.colors.textPrimary,
            )

            Row(
                modifier = Modifier.padding(start = SiteTheme.spacing.xl).weight(1f),
                horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Section.entries.forEach { section ->
                    NavTab(
                        label = section.label(),
                        selected = section == currentSection,
                        onClick = { onSectionSelected(section) },
                    )
                }
            }

            SearchBar(
                onResultSelected = { result ->
                    routeFromPath(result.id)?.let { onRouteSelected(it) }
                },
                results = { query ->
                    searchPages(query).map { entry ->
                        SearchResult(label = entry.label, id = entry.route.path)
                    }
                },
            )

            ThemeToggle(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
            )
        }
    }
}

@Composable
private fun NavTab(
    label: String,
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
                backgroundColor = SiteTheme.colors.surface,
                contentColor = SiteTheme.colors.textSecondary,
            )
        }

    Container(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        style =
            StyleDefaults.style(
                colors = colors,
                shapes =
                    StyleDefaults.shapes(
                        shape = RoundedCornerShape(SiteTheme.spacing.s),
                    ),
            ),
    ) {
        // Box without fillMaxSize so the NavTab sizes to text width in the horizontal Row.
        // propagateMinConstraints=true on Container gives Box minHeight=40dp, so Box becomes
        // (textWidth, 40dp) and contentAlignment centers the text vertically.
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = SiteTheme.typography.body.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}

private fun Section.label(): String =
    when (this) {
        Section.GettingStarted -> "Getting Started"
        Section.Components -> "Components"
        Section.Foundations -> "Foundations"
    }
