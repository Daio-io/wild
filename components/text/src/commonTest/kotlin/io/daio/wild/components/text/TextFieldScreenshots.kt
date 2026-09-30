// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.background
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
internal fun EmptyTextField() {
    val state = remember { TextFieldState() }
    ScreenshotSurface {
        TextField(state, cursorBrush = SolidColor(Color.Transparent), decorator = fieldDecorator("empty"))
    }
}

@Composable
internal fun ReadOnlyTextField() {
    val state = remember { TextFieldState("fixed text") }
    ScreenshotSurface {
        TextField(
            state,
            readOnly = true,
            cursorBrush = SolidColor(Color.Transparent),
            decorator = fieldDecorator("read only"),
        )
    }
}

@Composable
internal fun DisabledTextField() {
    val state = remember { TextFieldState("fixed text") }
    ScreenshotSurface {
        TextField(
            state,
            enabled = false,
            cursorBrush = SolidColor(Color.Transparent),
            decorator = fieldDecorator("disabled"),
        )
    }
}

@Composable
internal fun ThreeLineTextArea() {
    val state = remember { TextFieldState("line one\nline two\nline three") }
    ScreenshotSurface {
        TextArea(
            state,
            minLines = 3,
            maxLines = 3,
            cursorBrush = SolidColor(Color.Transparent),
            decorator = fieldDecorator("three lines"),
        )
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
