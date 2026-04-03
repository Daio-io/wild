// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.daio.wild.layout.divider.VerticalDivider
import io.daio.wild.site.navigation.Route
import io.daio.wild.site.navigation.Section
import io.daio.wild.site.navigation.Sidebar
import io.daio.wild.site.navigation.TopNav
import io.daio.wild.site.navigation.routeFromPath
import io.daio.wild.site.navigation.section
import io.daio.wild.site.navigation.sidebarGroupsForSection
import io.daio.wild.site.pages.PlaceholderPage
import io.daio.wild.site.theme.SiteTheme

@Composable
fun SiteApp(navController: NavHostController = rememberNavController()) {
    SiteTheme {
        // Derive currentRoute from the nav back stack so browser back/forward
        // automatically updates the selected tab and sidebar item.
        val currentEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            currentEntry?.destination?.route
                ?.let { routeFromPath(it) }
                ?: Route.GettingStarted
        val currentSection = currentRoute.section()
        val sidebarGroups = sidebarGroupsForSection(currentSection)

        Column(modifier = Modifier.fillMaxSize().background(SiteTheme.colors.background)) {
            TopNav(
                currentSection = currentSection,
                onSectionSelected = { section ->
                    navController.navigate(defaultRouteForSection(section).path) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )

            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (sidebarGroups.isNotEmpty()) {
                    Sidebar(
                        groups = sidebarGroups,
                        currentRoute = currentRoute,
                        onRouteSelected = { route ->
                            navController.navigate(route.path) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                    VerticalDivider(color = SiteTheme.colors.border)
                }

                NavHost(
                    navController = navController,
                    startDestination = Route.GettingStarted.path,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    composable(Route.GettingStarted.path) {
                        PlaceholderPage(
                            title = "Getting Started",
                            subtitle = "Learn how to install and use Wild primitives in your Compose Multiplatform project.",
                        )
                    }
                    composable(Route.Component.Button.path) {
                        PlaceholderPage(
                            title = "Button",
                            subtitle = "An interactive button primitive with style support.",
                        )
                    }
                    composable(Route.Component.Container.path) {
                        PlaceholderPage(
                            title = "Container",
                            subtitle = "A foundational layout component with optional interactivity.",
                        )
                    }
                    composable(Route.Component.Text.path) {
                        PlaceholderPage(
                            title = "Text",
                            subtitle = "A text component that integrates with content color and text style locals.",
                        )
                    }
                    composable(Route.Component.Icon.path) {
                        PlaceholderPage(
                            title = "Icon",
                            subtitle = "An icon component supporting Painter, ImageVector, and ImageBitmap.",
                        )
                    }
                    composable(Route.Component.ListItem.path) {
                        PlaceholderPage(
                            title = "ListItem",
                            subtitle = "A list item with optional leading and trailing content slots.",
                        )
                    }
                    composable(Route.Component.Toggleable.path) {
                        PlaceholderPage(
                            title = "Toggleable",
                            subtitle = "A toggleable primitive for building checkboxes, switches, and selectable items.",
                        )
                    }
                    composable(Route.Component.Divider.path) {
                        PlaceholderPage(
                            title = "Divider",
                            subtitle = "Horizontal and vertical dividers for separating content.",
                        )
                    }
                    composable(Route.Foundation.Style.path) {
                        PlaceholderPage(
                            title = "Style",
                            subtitle = "The style system for state-based visual properties.",
                        )
                    }
                    composable(Route.Foundation.ContentColor.path) {
                        PlaceholderPage(
                            title = "Content Color",
                            subtitle = "Composition local for propagating content color through the tree.",
                        )
                    }
                    composable(Route.Foundation.Modifier.path) {
                        PlaceholderPage(
                            title = "Modifier",
                            subtitle = "Utility modifiers like thenIf and thenIfNotNull.",
                        )
                    }
                    composable(Route.Foundation.InteractionState.path) {
                        PlaceholderPage(
                            title = "Interaction State",
                            subtitle = "Track focused, hovered, pressed, and selected states.",
                        )
                    }
                }
            }
        }
    }
}

private fun defaultRouteForSection(section: Section): Route =
    when (section) {
        Section.GettingStarted -> Route.GettingStarted
        Section.Components -> Route.Component.Button
        Section.Foundations -> Route.Foundation.Style
    }
