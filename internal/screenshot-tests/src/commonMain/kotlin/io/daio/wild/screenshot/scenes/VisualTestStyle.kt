// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot.scenes

import androidx.compose.ui.graphics.Color
import io.daio.wild.style.StyleDefaults

internal val visualTestStyle =
    StyleDefaults.style(
        colors =
            StyleDefaults.colors(
                backgroundColor = Color(0xFF243447),
                contentColor = Color.White,
                selectedBackgroundColor = Color(0xFF2E7D32),
                disabledBackgroundColor = Color(0xFF54606B),
            ),
    )
