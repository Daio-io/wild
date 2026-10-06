// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(io.daio.wild.foundation.ExperimentalWildApi::class)

package io.daio.wild.components.button

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.screenshot.rememberFocusedInteractionSource
import io.daio.wild.style.styleSpec

@Composable
internal fun EnabledButton() {
    ScreenshotSurface {
        Button(onClick = {}, style = ScreenshotTestStyle) {
            BasicText("enabled", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun DisabledButton() {
    ScreenshotSurface {
        Button(onClick = {}, enabled = false, style = ScreenshotTestStyle) {
            BasicText("disabled", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun FocusedButton() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        Button(onClick = {}, style = ScreenshotTestStyle, interactionSource = interactionSource) {
            BasicText("focused", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun SpecButton() {
    val spec =
        styleSpec(ScreenshotTestStyle) {
            if (focused) scale = 1.1f
        }
    ScreenshotSurface {
        Button(onClick = {}, style = spec) {
            BasicText("spec", style = TextStyle(color = LocalContentColor.current))
        }
    }
}
