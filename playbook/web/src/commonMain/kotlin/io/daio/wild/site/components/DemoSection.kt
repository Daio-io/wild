// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.theme.SiteTheme

@Composable
fun DemoSection(
    demos: List<Demo>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.m),
    ) {
        demos.forEach { demo ->
            DemoCard(demo)
        }
    }
}

@Composable
private fun DemoCard(
    demo: Demo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SiteTheme.spacing.s),
    ) {
        Text(
            text = demo.title,
            style = SiteTheme.typography.h3,
            color = SiteTheme.colors.textPrimary,
        )
        if (demo.description.isNotEmpty()) {
            Text(
                text = demo.description,
                style = SiteTheme.typography.bodySmall,
                color = SiteTheme.colors.textSecondary,
            )
        }
        val cardShape = RoundedCornerShape(SiteTheme.spacing.s)
        Container(
            modifier = Modifier.fillMaxWidth().heightIn(min = 245.dp),
            color = SiteTheme.colors.demoBackground,
            shape = cardShape,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(SiteTheme.spacing.l),
                contentAlignment = Alignment.Center,
            ) {
                demo.content()
            }
        }
    }
}
