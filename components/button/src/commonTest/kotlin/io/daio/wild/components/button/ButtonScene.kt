// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle

@Composable
internal fun ButtonScene() {
    ScreenshotSurface {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, style = ScreenshotTestStyle) {
                BasicText("enabled", style = TextStyle(color = LocalContentColor.current))
            }
            Button(onClick = {}, enabled = false, style = ScreenshotTestStyle) {
                BasicText("disabled", style = TextStyle(color = LocalContentColor.current))
            }
        }
    }
}
