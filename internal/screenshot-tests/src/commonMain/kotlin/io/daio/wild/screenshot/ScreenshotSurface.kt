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
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.content.ProvidesContentColor

@Composable
fun ScreenshotSurface(content: @Composable BoxScope.() -> Unit) =
    ProvidesContentColor(Color.Black) {
        Box(
            Modifier
                .width(480.dp)
                .background(Color.White)
                .padding(24.dp),
            content = content,
        )
    }

@Composable
internal fun SmokeScene() =
    ScreenshotSurface {
        Container(color = Color(0xFF17324D), contentColor = Color.White) { Text("Wild") }
    }
