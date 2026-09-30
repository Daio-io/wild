// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun RegularText() {
    ScreenshotSurface {
        Text("Wild regular", fontSize = 18.sp)
    }
}

@Composable
internal fun BoldClippedText() {
    ScreenshotSurface {
        Text(
            "Wild bold clipped text that exceeds the available width",
            modifier = Modifier.width(200.dp),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
