// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(io.daio.wild.foundation.ExperimentalWildApi::class)

package io.daio.wild.container

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.screenshot.rememberFocusedInteractionSource
import io.daio.wild.style.Border
import io.daio.wild.style.styleSpec

private val Navy = Color(0xFF17324D)

@Composable
internal fun StaticContainer() {
    ScreenshotSurface {
        Container(
            color = Navy,
            contentColor = Color.White,
            shape = RoundedCornerShape(12.dp),
            border = Border(width = 2.dp, color = Color(0xFF4F9DCE), shape = RoundedCornerShape(12.dp)),
        ) {
            BasicText("static", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun SelectedContainer() {
    ScreenshotSurface {
        Container(onClick = {}, selected = true, style = ScreenshotTestStyle) {
            BasicText("selected", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun DisabledContainer() {
    ScreenshotSurface {
        Container(onClick = {}, enabled = false, style = ScreenshotTestStyle) {
            BasicText("disabled", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun FocusedContainer() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        Container(onClick = {}, style = ScreenshotTestStyle, interactionSource = interactionSource) {
            BasicText("focused", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun SpecContainer() {
    val spec =
        styleSpec(ScreenshotTestStyle) {
            if (focused) scale = 1.1f
        }
    ScreenshotSurface {
        Container(onClick = {}, style = spec) {
            BasicText("spec", Modifier.padding(12.dp), style = TextStyle(color = LocalContentColor.current))
        }
    }
}
