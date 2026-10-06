// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.rememberFocusedInteractionSource

private val BaseNavy = Color(0xFF243447)
private val OverrideAmber = Color(0xFFE65100)

/**
 * Focused [StyleSpec] chrome: base tables seed navy, then ordered overrides win for focused
 * color, scale, and border so Spec rendering regressions show in the pixel baseline.
 */
@OptIn(ExperimentalWildApi::class)
@Composable
internal fun FocusedStyleSpec() {
    val interactionSource = rememberFocusedInteractionSource()
    val focusedScale: ComponentStyleScope.() -> Unit = {
        if (focused) {
            scale = 1.2f
        }
    }
    val focusedChrome: ComponentStyleScope.() -> Unit = {
        if (focused) {
            color = OverrideAmber
            border =
                Border(
                    width = 3.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                )
        }
    }
    val spec =
        remember {
            styleSpec(
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = BaseNavy,
                            contentColor = Color.White,
                            focusedBackgroundColor = Color(0xFF1565C0),
                            focusedContentColor = Color.White,
                        ),
                    scale = StyleDefaults.scale(scale = 1f, focusedScale = 1f),
                    shapes = StyleDefaults.shapes(shape = RoundedCornerShape(12.dp)),
                ),
                focusedScale,
            ).then(focusedChrome)
        }
    ScreenshotSurface {
        Box(
            Modifier
                .size(160.dp)
                .interactionStyle(interactionSource = interactionSource, style = spec),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                "focused spec",
                Modifier.padding(12.dp),
                style = TextStyle(color = Color.White),
            )
        }
    }
}
