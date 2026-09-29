// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container

/**
 * Provides the fixed 480 dp screenshot surface and the default content color for a screenshot.
 *
 * @param content content rendered inside the screenshot surface
 * @since 0.4.0
 */
@Composable
fun ScreenshotSurface(content: @Composable BoxScope.() -> Unit) =
    Container(
        modifier = Modifier.width(480.dp),
        color = Color.Black,
        contentColor = Color.White,
    ) {
        Box(Modifier.padding(24.dp), content = content)
    }

@Composable
internal fun SmokeScene() =
    ScreenshotSurface {
        Text("Wild")
    }
