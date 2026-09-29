// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle

@Composable
internal fun ListItemScene() {
    ScreenshotSurface {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ListItem(
                onClick = {},
                leadingContent = { Box(Modifier.size(24.dp).background(Color(0xFF2F80ED))) },
                trailingContent = {
                    BasicText("+", style = TextStyle(color = LocalContentColor.current))
                },
                style = ScreenshotTestStyle,
            ) {
                BasicText("unselected item", style = TextStyle(color = LocalContentColor.current))
            }
            ListItem(
                onClick = {},
                leadingContent = { Box(Modifier.size(24.dp).background(Color(0xFF2F80ED))) },
                trailingContent = {
                    BasicText("+", style = TextStyle(color = LocalContentColor.current))
                },
                selected = true,
                style = ScreenshotTestStyle,
            ) {
                BasicText("selected item", style = TextStyle(color = LocalContentColor.current))
            }
        }
    }
}
