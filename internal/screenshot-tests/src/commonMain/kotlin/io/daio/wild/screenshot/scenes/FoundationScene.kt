// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.content.ProvidesContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.style.Border

private val Navy = Color(0xFF17324D)

@Composable
internal fun FoundationScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp)) {
            ProvidesContentColor(Navy) { Text("content color") }
            Container(
                color = Navy,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = Border(width = 2.dp, color = Color(0xFF4F9DCE), shape = RoundedCornerShape(12.dp)),
            ) { Text("static", Modifier.padding(12.dp)) }
            Container(
                onClick = {},
                selected = true,
                style = visualTestStyle,
                modifier = Modifier.padding(top = 8.dp),
            ) { Text("selected", Modifier.padding(12.dp)) }
            Container(
                onClick = {},
                enabled = false,
                style = visualTestStyle,
                modifier = Modifier.padding(top = 8.dp),
            ) { Text("disabled", Modifier.padding(12.dp)) }
        }
    }
}
