// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import io.daio.wild.components.text.Text
import io.daio.wild.site.theme.SiteTheme

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val shape = RoundedCornerShape(SiteTheme.spacing.s)
    Column(
        modifier = modifier.fillMaxWidth().clip(shape).background(SiteTheme.colors.codeBackground, shape),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = SiteTheme.typography.label,
                color = SiteTheme.colors.textSecondary,
                modifier = Modifier.padding(start = SiteTheme.spacing.m, top = SiteTheme.spacing.s),
            )
        }
        val highlighted = remember(code) { highlightCode(code) }
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
