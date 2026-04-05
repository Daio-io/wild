// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.theme.SiteTheme

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    tabs: List<String> = emptyList(),
    tabContents: Map<String, String> = emptyMap(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val displayedCode =
        if (tabs.isNotEmpty() && tabContents.isNotEmpty()) {
            tabContents[tabs.getOrNull(selectedTab)] ?: code
        } else {
            code
        }

    val shape = RoundedCornerShape(SiteTheme.spacing.s)
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, SiteTheme.colors.border, shape)
                .background(SiteTheme.colors.codeBackground, shape),
    ) {
        if (tabs.isNotEmpty()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SiteTheme.spacing.m),
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = tab,
                        isActive = index == selectedTab,
                        onClick = { selectedTab = index },
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SiteTheme.colors.border),
            )
        }
        val highlighted = remember(displayedCode) { highlightCode(displayedCode) }
        SelectionContainer {
            Text(
                text = highlighted,
                style = SiteTheme.typography.code,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(SiteTheme.spacing.m),
            )
        }
    }
}

@Composable
private fun Tab(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Container(
            onClick = onClick,
            modifier = Modifier.padding(top = SiteTheme.spacing.s),
        ) {
            Text(
                text = text,
                style = SiteTheme.typography.bodySmall,
                color =
                    if (isActive) {
                        SiteTheme.colors.accentText
                    } else {
                        SiteTheme.colors.textSecondary
                    },
                modifier =
                    Modifier.padding(
                        horizontal = SiteTheme.spacing.s,
                        vertical = SiteTheme.spacing.xs,
                    ),
            )
        }
        if (isActive) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(SiteTheme.colors.accent),
            )
        } else {
            Box(modifier = Modifier.height(2.dp))
        }
    }
}
