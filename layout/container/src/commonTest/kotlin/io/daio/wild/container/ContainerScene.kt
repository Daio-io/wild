// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.style.Border

private val Navy = Color(0xFF17324D)

@Composable
internal fun ContainerScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp)) {
            Container(
                color = Navy,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = Border(width = 2.dp, color = Color(0xFF4F9DCE), shape = RoundedCornerShape(12.dp)),
            ) {
                BasicText("static", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
            }
            Container(
                onClick = {},
                selected = true,
                style = ScreenshotTestStyle,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                BasicText("selected", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
            }
            Container(
                onClick = {},
                enabled = false,
                style = ScreenshotTestStyle,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                BasicText("disabled", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
            }
        }
    }
}
