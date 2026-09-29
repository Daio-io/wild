// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.screenshot.ScreenshotSurface
import io.daio.wild.screenshot.ScreenshotTestStyle

@Composable
internal fun CheckboxScene() {
    ScreenshotSurface {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ToggleRow("off") {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    style = ScreenshotTestStyle,
                    indicator = { Box(Modifier.size(20.dp).border(2.dp, Color.White)) },
                )
            }
            ToggleRow("on") {
                Checkbox(
                    checked = true,
                    onCheckedChange = {},
                    style = ScreenshotTestStyle,
                    indicator = {
                        Box(Modifier.size(20.dp).background(Color(0xFF43A047))) {
                            BasicText("✓", style = TextStyle(color = Color.White))
                        }
                    },
                )
            }
            ToggleRow("indeterminate") {
                TriStateCheckbox(
                    ToggleableState.Indeterminate,
                    {},
                    style = ScreenshotTestStyle,
                    indicator = {
                        Box(Modifier.size(20.dp).background(Color(0xFFFFA000))) {
                            BasicText("–", style = TextStyle(color = Color.White))
                        }
                    },
                )
            }
            ToggleRow("disabled on") {
                Checkbox(
                    true,
                    {},
                    enabled = false,
                    style = ScreenshotTestStyle,
                    indicator = {
                        Box(Modifier.size(20.dp).background(Color(0xFF607D8B))) {
                            BasicText("✓", style = TextStyle(color = Color.White))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    control: @Composable () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        control()
        BasicText(label, style = TextStyle(color = LocalContentColor.current))
    }
}
