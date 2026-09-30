// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface

private val testVector =
    ImageVector.Builder(
        name = "VisualTestVector",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(12f, 2f)
            lineTo(22f, 12f)
            lineTo(12f, 22f)
            lineTo(2f, 12f)
            close()
        }
    }.build()

private val testBitmap =
    ImageBitmap(8, 8).also { bitmap ->
        Canvas(bitmap).drawRect(0f, 0f, 8f, 8f, Paint().apply { color = Color.White })
    }

@Composable
internal fun VectorIcon() {
    ScreenshotSurface {
        Icon(testVector, "test", tint = Color(0xFF43A047))
    }
}

@Composable
internal fun BitmapIcon() {
    ScreenshotSurface {
        Icon(testBitmap, "bitmap", tint = Color(0xFF8E44AD))
    }
}
