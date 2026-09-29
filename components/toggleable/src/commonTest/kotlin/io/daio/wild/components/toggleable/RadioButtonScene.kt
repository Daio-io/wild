// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle

@Composable
internal fun RadioButtonScene() {
    ScreenshotSurface {
        Row(
            Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                false,
                {},
                style = ScreenshotTestStyle,
                indicator = {
                    Box(Modifier.size(20.dp).border(2.dp, Color.White, RoundedCornerShape(10.dp)))
                },
            )
            RadioButton(
                true,
                {},
                style = ScreenshotTestStyle,
                indicator = {
                    Box(Modifier.size(20.dp).background(Color(0xFF43A047), RoundedCornerShape(10.dp)))
                },
            )
        }
    }
}
