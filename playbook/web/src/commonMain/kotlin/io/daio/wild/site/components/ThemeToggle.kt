// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Container(
        onClick = onToggleTheme,
        modifier = modifier.height(36.dp),
        style =
            StyleDefaults.style(
                colors =
                    StyleDefaults.colors(
                        backgroundColor = SiteTheme.colors.background,
                        contentColor = SiteTheme.colors.textSecondary,
                        hoveredBackgroundColor = SiteTheme.colors.accentSubtle,
                        hoveredContentColor = SiteTheme.colors.accent,
                    ),
                shapes =
                    StyleDefaults.shapes(
                        shape = RoundedCornerShape(SiteTheme.spacing.s),
                    ),
                borders =
                    StyleDefaults.borders(
                        border =
                            Border(
                                borderStroke = BorderStroke(1.dp, SiteTheme.colors.border),
                                shape = RoundedCornerShape(SiteTheme.spacing.s),
                            ),
                    ),
            ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (isDarkTheme) "Light" else "Dark",
                modifier = Modifier.padding(horizontal = 12.dp),
                style = SiteTheme.typography.body.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}
