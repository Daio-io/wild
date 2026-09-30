// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle
import io.daio.wild.screenshot.rememberFocusedInteractionSource

@Composable
internal fun UncheckedCheckbox() {
    ScreenshotSurface {
        Checkbox(
            checked = false,
            onCheckedChange = {},
            style = ScreenshotTestStyle,
            indicator = { Box(Modifier.size(20.dp).border(2.dp, Color.White)) },
        )
    }
}

@Composable
internal fun CheckedCheckbox() {
    ScreenshotSurface {
        Checkbox(
            checked = true,
            onCheckedChange = {},
            style = ScreenshotTestStyle,
            indicator = {
                Box(Modifier.size(20.dp).background(Color(0xFF43A047))) {
                    BasicText("✓", style = TextStyle(color = Color.White))
                }
            },
        )
    }
}

@Composable
internal fun IndeterminateCheckbox() {
    ScreenshotSurface {
        TriStateCheckbox(
            ToggleableState.Indeterminate,
            {},
            style = ScreenshotTestStyle,
            indicator = {
                Box(Modifier.size(20.dp).background(Color(0xFFFFA000))) {
                    BasicText("–", style = TextStyle(color = Color.White))
                }
            },
        )
    }
}

@Composable
internal fun DisabledCheckedCheckbox() {
    ScreenshotSurface {
        Checkbox(
            true,
            {},
            enabled = false,
            style = ScreenshotTestStyle,
            indicator = {
                Box(Modifier.size(20.dp).background(Color(0xFF607D8B))) {
                    BasicText("✓", style = TextStyle(color = Color.White))
                }
            },
        )
    }
}

@Composable
internal fun FocusedCheckbox() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        Checkbox(
            checked = false,
            onCheckedChange = {},
            style = ScreenshotTestStyle,
            interactionSource = interactionSource,
            indicator = { Box(Modifier.size(20.dp).border(2.dp, Color.White)) },
        )
    }
}
