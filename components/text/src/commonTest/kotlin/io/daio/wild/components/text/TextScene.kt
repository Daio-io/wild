// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun TextScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp)) {
            Text("Wild regular", fontSize = 18.sp)
            Text(
                "Wild bold clipped text that exceeds the available width",
                modifier = Modifier.width(200.dp),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
