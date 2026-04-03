// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.components.text.Text
import io.daio.wild.site.theme.SiteTheme

@Composable
fun PlaceholderPage(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(SiteTheme.spacing.xl)) {
        Text(
            text = title,
            style = SiteTheme.typography.h1,
            color = SiteTheme.colors.textPrimary,
        )
        Text(
            text = subtitle,
            style = SiteTheme.typography.body,
            color = SiteTheme.colors.textSecondary,
            modifier = Modifier.padding(top = SiteTheme.spacing.s),
        )
    }
}
