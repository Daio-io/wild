// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.site.theme.SiteTheme

@Composable
fun PropsTable(
    props: List<Prop>,
    modifier: Modifier = Modifier,
) {
    val sorted = remember(props) { props.sortedByDescending { it.required } }

    val shape = RoundedCornerShape(SiteTheme.spacing.s)
    Column(
        modifier =
            modifier.fillMaxWidth()
                .clip(shape)
                .background(SiteTheme.colors.surface, shape),
    ) {
        // Header row
        PropsRow(
            name = "Param",
            type = "Type",
            default = "Default",
            isHeader = true,
        )
        HorizontalDivider(color = SiteTheme.colors.border)

        sorted.forEachIndexed { index, prop ->
            PropsRow(
                name = prop.name,
                type = prop.type,
                default = prop.default ?: if (prop.required) "required" else "-",
                isRequired = prop.required,
            )
            if (index < sorted.lastIndex) {
                HorizontalDivider(color = SiteTheme.colors.border)
            }
        }
    }
}

@Composable
private fun PropsRow(
    name: String,
    type: String,
    default: String,
    isHeader: Boolean = false,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = SiteTheme.spacing.m, vertical = SiteTheme.spacing.s),
        horizontalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
    ) {
        Text(
            text = name,
            style = if (isHeader) SiteTheme.typography.label else SiteTheme.typography.code,
            color = if (isRequired) SiteTheme.colors.accent else SiteTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = type,
            style = if (isHeader) SiteTheme.typography.label else SiteTheme.typography.code,
            color = SiteTheme.colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = default,
            style = if (isHeader) SiteTheme.typography.label else SiteTheme.typography.code,
            color = SiteTheme.colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}
