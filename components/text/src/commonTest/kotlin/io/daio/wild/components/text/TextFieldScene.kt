// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun TextFieldScene() {
    val empty = remember { TextFieldState() }
    val populated = remember { TextFieldState("fixed text") }
    val cursor = SolidColor(Color.Transparent)
    ScreenshotSurface {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(empty, cursorBrush = cursor, decorator = fieldDecorator("empty"))
            TextField(populated, readOnly = true, cursorBrush = cursor, decorator = fieldDecorator("read only"))
            TextField(populated, enabled = false, cursorBrush = cursor, decorator = fieldDecorator("disabled"))
        }
    }
}

@Composable
internal fun TextAreaScene() {
    val area = remember { TextFieldState("line one\nline two\nline three") }
    val cursor = SolidColor(Color.Transparent)
    ScreenshotSurface {
        Column(Modifier.padding(8.dp)) {
            TextArea(area, minLines = 3, maxLines = 3, cursorBrush = cursor, decorator = fieldDecorator("three lines"))
        }
    }
}

@Composable
internal fun fieldDecorator(label: String) =
    TextFieldDecorator { innerTextField ->
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF243447))
                .padding(8.dp),
        ) {
            Text(label)
            innerTextField()
        }
    }
