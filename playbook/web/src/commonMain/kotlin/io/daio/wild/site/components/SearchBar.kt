// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults

@Composable
fun SearchBar(
    onResultSelected: (SearchResult) -> Unit,
    results: (query: String) -> List<SearchResult>,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    val currentResults = remember(query) { results(query) }

    Box(modifier = modifier.width(220.dp).height(36.dp)) {
        Container(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            color = SiteTheme.colors.background,
            shape = RoundedCornerShape(SiteTheme.spacing.s),
            border =
                Border(
                    borderStroke = BorderStroke(1.dp, SiteTheme.colors.border),
                    shape = RoundedCornerShape(SiteTheme.spacing.s),
                ),
        ) {
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    showResults = it.isNotBlank()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 12.dp),
                textStyle =
                    SiteTheme.typography.body.copy(
                        color = SiteTheme.colors.textPrimary,
                    ),
                cursorBrush = SolidColor(SiteTheme.colors.accent),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Search pages...",
                                    style = SiteTheme.typography.body,
                                    color = SiteTheme.colors.textSecondary,
                                )
                            }
                            innerTextField()
                        }
                    }
                },
            )
        }

        if (showResults && currentResults.isNotEmpty()) {
            SearchResultsDropdown(
                results = currentResults,
                onResultSelected = { result ->
                    onResultSelected(result)
                    query = ""
                    showResults = false
                },
                onDismiss = { showResults = false },
            )
        }
    }
}

@Composable
private fun SearchResultsDropdown(
    results: List<SearchResult>,
    onResultSelected: (SearchResult) -> Unit,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(0, with(density) { 40.dp.roundToPx() }),
        onDismissRequest = onDismiss,
    ) {
        Container(
            modifier = Modifier.width(220.dp),
            color = SiteTheme.colors.surface,
            shape = RoundedCornerShape(SiteTheme.spacing.s),
            border =
                Border(
                    borderStroke = BorderStroke(1.dp, SiteTheme.colors.border),
                    shape = RoundedCornerShape(SiteTheme.spacing.s),
                ),
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                results.forEach { result ->
                    Container(
                        onClick = { onResultSelected(result) },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        style =
                            StyleDefaults.style(
                                colors =
                                    StyleDefaults.colors(
                                        backgroundColor = SiteTheme.colors.surface,
                                        contentColor = SiteTheme.colors.textPrimary,
                                        hoveredBackgroundColor = SiteTheme.colors.accentSubtle,
                                        hoveredContentColor = SiteTheme.colors.accent,
                                    ),
                            ),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = result.label,
                                style =
                                    SiteTheme.typography.body.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SearchResult(
    val label: String,
    val id: String,
)
