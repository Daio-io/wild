// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.ui.graphics.Color
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults

/**
 * Explicit non-transparent style for screenshot matrix cases.
 *
 * @since 0.4.0
 */
val ScreenshotTestStyle: Style =
    StyleDefaults.style(
        colors =
            StyleDefaults.colors(
                backgroundColor = Color(0xFF243447),
                contentColor = Color.White,
                focusedBackgroundColor = Color(0xFF1565C0),
                focusedContentColor = Color.White,
                selectedBackgroundColor = Color(0xFF2E7D32),
                disabledBackgroundColor = Color(0xFF54606B),
            ),
    )
