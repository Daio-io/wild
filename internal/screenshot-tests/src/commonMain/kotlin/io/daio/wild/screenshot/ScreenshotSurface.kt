// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Provides the fixed 480 dp screenshot surface for a screenshot capture.
 *
 * Consumers supply their own component content and content colors inside the surface.
 *
 * @param content content rendered inside the screenshot surface
 * @since 0.4.0
 */
@Composable
fun ScreenshotSurface(content: @Composable BoxScope.() -> Unit) =
    Box(
        modifier =
            Modifier
                .width(480.dp)
                .background(Color.Black)
                .padding(24.dp),
        content = content,
    )
