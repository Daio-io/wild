// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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

@Composable
private fun Leading() {
    Box(Modifier.size(24.dp).background(Color(0xFF2F80ED)))
}

@Composable
private fun Trailing() {
    BasicText("+", style = TextStyle(color = LocalContentColor.current))
}

@Composable
internal fun EnabledListItem() {
    ScreenshotSurface {
        ListItem(
            onClick = {},
            leadingContent = { Leading() },
            trailingContent = { Trailing() },
            style = ScreenshotTestStyle,
        ) {
            BasicText("enabled item", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun SelectedListItem() {
    ScreenshotSurface {
        ListItem(
            onClick = {},
            leadingContent = { Leading() },
            trailingContent = { Trailing() },
            selected = true,
            style = ScreenshotTestStyle,
        ) {
            BasicText("selected item", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun DisabledListItem() {
    ScreenshotSurface {
        ListItem(
            onClick = {},
            leadingContent = { Leading() },
            trailingContent = { Trailing() },
            enabled = false,
            style = ScreenshotTestStyle,
        ) {
            BasicText("disabled item", style = TextStyle(color = LocalContentColor.current))
        }
    }
}

@Composable
internal fun FocusedListItem() {
    val interactionSource = rememberFocusedInteractionSource()
    ScreenshotSurface {
        ListItem(
            onClick = {},
            leadingContent = { Leading() },
            trailingContent = { Trailing() },
            style = ScreenshotTestStyle,
            interactionSource = interactionSource,
        ) {
            BasicText("focused item", style = TextStyle(color = LocalContentColor.current))
        }
    }
}
