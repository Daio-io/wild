// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import io.daio.wild.screenshot.ScreenshotSurface

private val Navy = Color(0xFF17324D)

@Composable
internal fun ContentColorScene() {
    ScreenshotSurface {
        ProvidesContentColor(Navy) {
            BasicText("content color", style = TextStyle(color = LocalContentColor.current))
        }
    }
}
