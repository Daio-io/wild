// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun HorizontalDividerScreenshot() {
    ScreenshotSurface {
        HorizontalDivider(color = Color(0xFF2F80ED), thickness = 2.dp)
    }
}

@Composable
internal fun VerticalDividerScreenshot() {
    ScreenshotSurface {
        VerticalDivider(Modifier.height(40.dp), color = Color(0xFFE53935), thickness = 2.dp)
    }
}
