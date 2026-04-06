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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.daio.wild.site.pages.GettingStartedPage
import io.daio.wild.site.pages.components.ButtonPage
import io.daio.wild.site.pages.components.ContainerPage
import io.daio.wild.site.pages.components.DividerPage
import io.daio.wild.site.pages.components.IconPage
import io.daio.wild.site.pages.components.ListItemPage
import io.daio.wild.site.pages.components.TextPage
import io.daio.wild.site.pages.components.ToggleablePage
import io.daio.wild.site.pages.foundations.ContentColorPage
import io.daio.wild.site.pages.foundations.InteractionStatePage
import io.daio.wild.site.pages.foundations.ModifierPage
import io.daio.wild.site.pages.foundations.StylePage
import io.daio.wild.site.theme.DarkSiteColors
import io.daio.wild.site.theme.LightSiteColors
import io.daio.wild.site.theme.SiteTheme

@Composable
fun SiteApp(navController: NavHostController = rememberNavController()) {
    var isDarkTheme by remember { mutableStateOf(true) }
    val colors = if (isDarkTheme) DarkSiteColors else LightSiteColors

    SiteTheme(colors = colors) {
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
                isDarkTheme = isDarkTheme,
                onToggleTheme = { isDarkTheme = !isDarkTheme },
                onRouteSelected = { route ->
                    navController.navigate(route.path) {
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
                        GettingStartedPage(
                            onNavigateToComponents = {
                                navController.navigate(Route.Component.Button.path) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToFoundations = {
                                navController.navigate(Route.Foundation.Style.path) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                    composable(Route.Component.Button.path) {
                        ButtonPage()
                    }
                    composable(Route.Component.Container.path) {
                        ContainerPage()
                    }
                    composable(Route.Component.Text.path) {
                        TextPage()
                    }
                    composable(Route.Component.Icon.path) {
                        IconPage()
                    }
                    composable(Route.Component.ListItem.path) {
                        ListItemPage()
                    }
                    composable(Route.Component.Toggleable.path) {
                        ToggleablePage()
                    }
                    composable(Route.Component.Divider.path) {
                        DividerPage()
                    }
                    composable(Route.Foundation.Style.path) {
                        StylePage()
                    }
                    composable(Route.Foundation.ContentColor.path) {
                        ContentColorPage()
                    }
                    composable(Route.Foundation.Modifier.path) {
                        ModifierPage()
                    }
                    composable(Route.Foundation.InteractionState.path) {
                        InteractionStatePage()
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
