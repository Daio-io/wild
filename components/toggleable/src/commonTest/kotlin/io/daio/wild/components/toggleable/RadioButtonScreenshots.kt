// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.screenshot.rememberFocusedInteractionSource

@Composable
internal fun UncheckedRadioButton() {
    ScreenshotSurface {
        RadioButton(
            false,
            {},
            style = ScreenshotTestStyle,
            indicator = {
                Box(Modifier.size(20.dp).border(2.dp, Color.White, RoundedCornerShape(10.dp)))
            },
        )
    }
}

@Composable
internal fun CheckedRadioButton() {
    ScreenshotSurface {
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

@Composable
internal fun FocusedRadioButton() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        RadioButton(
            false,
            {},
            style = ScreenshotTestStyle,
            interactionSource = interactionSource,
            indicator = {
                Box(Modifier.size(20.dp).border(2.dp, Color.White, RoundedCornerShape(10.dp)))
            },
        )
    }
}
