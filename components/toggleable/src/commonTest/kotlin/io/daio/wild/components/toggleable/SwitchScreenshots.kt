// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.screenshot.rememberFocusedInteractionSource

@Composable
internal fun OffSwitch() {
    ScreenshotSurface {
        Switch(false, {}, style = ScreenshotTestStyle) { SwitchIndicator(it) }
    }
}

@Composable
internal fun OnSwitch() {
    ScreenshotSurface {
        Switch(true, {}, style = ScreenshotTestStyle) { SwitchIndicator(it) }
    }
}

@Composable
internal fun FocusedSwitch() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        Switch(false, {}, style = ScreenshotTestStyle, interactionSource = interactionSource) {
            SwitchIndicator(it)
        }
    }
}

@Composable
private fun SwitchIndicator(checked: Boolean) {
    Box(
        Modifier
            .width(44.dp)
            .height(24.dp)
            .background(if (checked) Color(0xFF2E7D32) else Color(0xFF5F6B76), RoundedCornerShape(12.dp)),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Spacer(Modifier.padding(3.dp).size(18.dp).background(Color.White, RoundedCornerShape(9.dp)))
    }
}
