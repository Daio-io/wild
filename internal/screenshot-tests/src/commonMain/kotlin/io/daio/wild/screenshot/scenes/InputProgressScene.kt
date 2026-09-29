// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.daio.wild.components.progress.CircularProgressIndicator
import io.daio.wild.components.progress.LinearProgressIndicator
import io.daio.wild.components.text.Text
import io.daio.wild.components.text.TextArea
import io.daio.wild.components.text.TextField
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
fun InputProgressScene() {
    val empty = remember { TextFieldState() }
    val populated = remember { TextFieldState("fixed text") }
    val area = remember { TextFieldState("line one\nline two\nline three") }
    val cursor = SolidColor(Color.Transparent)
    ScreenshotSurface {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(empty, cursorBrush = cursor, decorator = fieldDecorator("empty"))
            TextField(populated, readOnly = true, cursorBrush = cursor, decorator = fieldDecorator("read only"))
            TextField(populated, enabled = false, cursorBrush = cursor, decorator = fieldDecorator("disabled"))
            TextArea(area, minLines = 3, maxLines = 3, cursorBrush = cursor, decorator = fieldDecorator("three lines"))
            LinearProgressIndicator(progress = { 0f }, Modifier.fillMaxWidth())
            LinearProgressIndicator(progress = { .5f }, Modifier.fillMaxWidth())
            LinearProgressIndicator(progress = { 1f }, Modifier.fillMaxWidth())
            LinearProgressIndicator(Modifier.fillMaxWidth())
            CircularProgressIndicator(progress = { 0f })
            CircularProgressIndicator(progress = { .5f })
            CircularProgressIndicator(progress = { 1f })
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun fieldDecorator(label: String) =
    androidx.compose.foundation.text.input.TextFieldDecorator { innerTextField ->
        io.daio.wild.container.Container(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF243447),
            contentColor = Color.White,
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(label)
                innerTextField()
            }
        }
    }
