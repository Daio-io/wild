// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.site.theme.SiteTheme

@Composable
fun ComponentPage(
    data: ComponentPageData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(SiteTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.xl),
    ) {
        // Section 1: Header
        Column(verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s)) {
            Text(
                text = data.name,
                style = SiteTheme.typography.h1,
                color = SiteTheme.colors.textPrimary,
            )
            Text(
                text = data.description,
                style = SiteTheme.typography.body,
                color = SiteTheme.colors.textSecondary,
            )
        }

        HorizontalDivider(color = SiteTheme.colors.border)

        // Section 2: Live Demo
        if (data.demos.isNotEmpty()) {
            SectionHeader("Demo")
            DemoSection(demos = data.demos)
        }

        // Section 3: Installation
        SectionHeader("Installation")
        CodeBlock(
            code = "implementation(\"${data.module}:<version>\")",
            label = "build.gradle.kts",
        )

        // Section 4: Usage
        SectionHeader("Usage")
        CodeBlock(code = data.usage)

        // Section 5: API Reference
        if (data.props.isNotEmpty()) {
            SectionHeader("API Reference")
            PropsTable(props = data.props)
        }

        // Section 6: Platform Availability
        if (data.platforms.isNotEmpty()) {
            SectionHeader("Platform Availability")
            PlatformBadges(platforms = data.platforms)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = SiteTheme.typography.h2,
        color = SiteTheme.colors.textPrimary,
        modifier = modifier,
    )
}
