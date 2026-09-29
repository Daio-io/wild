// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun SmokeScene() =
    ScreenshotSurface {
        Container(
            color = Color(0xFF17324D),
            contentColor = Color.White,
        ) {
            BasicText(
                text = "Wild",
                style = TextStyle(color = LocalContentColor.current),
            )
        }
    }
