// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.theme.SiteTheme

@Composable
fun PlatformBadges(
    platforms: List<Platform>,
    modifier: Modifier = Modifier,
) {
    val badgeShape = RoundedCornerShape(SiteTheme.spacing.xs)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
    ) {
        platforms.forEach { platform ->
            Container(
                modifier =
                    Modifier.border(
                        1.dp,
                        SiteTheme.colors.border,
                        badgeShape,
                    ),
                color = SiteTheme.colors.surface,
                shape = badgeShape,
            ) {
                Text(
                    text = platform.label,
                    style = SiteTheme.typography.label,
                    color = SiteTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = SiteTheme.spacing.s, vertical = SiteTheme.spacing.xs),
                )
            }
        }
    }
}
