// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.components.icon.Icon
import io.daio.wild.components.text.Text
import io.daio.wild.layout.divider.HorizontalDivider
import io.daio.wild.layout.divider.VerticalDivider
import io.daio.wild.screenshot.ScreenshotSurface

internal val testVector =
    ImageVector.Builder(
        name = "VisualTestVector",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = androidx.compose.ui.graphics.SolidColor(Color.White)) {
            moveTo(12f, 2f)
            lineTo(22f, 12f)
            lineTo(12f, 22f)
            lineTo(2f, 12f)
            close()
        }
    }.build()

internal val testBitmap = ImageBitmap(8, 8)

@Composable
fun LayoutTextIconScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp)) {
            Text("Wild regular", fontSize = 18.sp)
            Text(
                "Wild bold clipped",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            HorizontalDivider(color = Color(0xFF2F80ED), thickness = 2.dp)
            Row(Modifier.padding(top = 12.dp)) {
                VerticalDivider(Modifier.height(40.dp), color = Color(0xFFE53935), thickness = 2.dp)
                Icon(testVector, "test", tint = Color(0xFF43A047), modifier = Modifier.padding(start = 12.dp))
                Icon(testBitmap, "bitmap", tint = Color(0xFF8E44AD), modifier = Modifier.padding(start = 12.dp))
            }
        }
    }
}
