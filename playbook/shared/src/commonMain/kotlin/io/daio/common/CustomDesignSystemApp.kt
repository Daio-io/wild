// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.content.ProvidesContentColor
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.clickable

@Composable
fun CustomDesignSystemApp(modifier: Modifier = Modifier) {
    DsTheme {
        LazyColumn(
            modifier =
                modifier.fillMaxSize()
                    .background(color = Theme.colors.background)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(100) {
                CustomContainer(
                    modifier =
                        Modifier.clickable(
                            style =
                                StyleDefaults.style(
                                    StyleDefaults.colors(hoveredBackgroundColor = Color.Red),
                                ),
                        ) {
                            println("Clicked")
                        },
                ) {
                    val contentColor = LocalContentColor.current
                    BasicText(
                        text = "Click Me",
                        color = { contentColor },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
fun CustomContainer(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = Theme.shapes.l,
    content: @Composable BoxScope.() -> Unit,
) {
    ProvidesContentColor(color = Theme.colors.onContainer) {
        Box(
            modifier = modifier,
            content = content,
        )
    }
}
