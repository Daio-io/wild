// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun DividerScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp)) {
            HorizontalDivider(color = Color(0xFF2F80ED), thickness = 2.dp)
            Row(Modifier.padding(top = 12.dp)) {
                VerticalDivider(Modifier.height(40.dp), color = Color(0xFFE53935), thickness = 2.dp)
            }
        }
    }
}
