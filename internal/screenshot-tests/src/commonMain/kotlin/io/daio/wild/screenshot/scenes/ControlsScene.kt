// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.daio.wild.components.button.Button
import io.daio.wild.components.listitem.ListItem
import io.daio.wild.components.text.Text
import io.daio.wild.components.toggleable.Checkbox
import io.daio.wild.components.toggleable.RadioButton
import io.daio.wild.components.toggleable.Switch
import io.daio.wild.components.toggleable.TriStateCheckbox
import io.daio.wild.screenshot.ScreenshotSurface
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

@Composable
fun ControlsScene() {
    ScreenshotSurface {
        Column(Modifier.width(432.dp).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, style = visualTestStyle) { Text("enabled") }
                Button(onClick = {}, enabled = false, style = visualTestStyle) { Text("disabled") }
            }
            ListItem(
                onClick = {},
                leadingContent = { Box(Modifier.size(24.dp).background(Color(0xFF2F80ED))) },
                trailingContent = { Text("+") },
                selected = true,
                style = visualTestStyle,
            ) { Text("selected item") }
            ToggleRow("Checkbox") {
                Checkbox(checked = false, onCheckedChange = {
                }, style = visualTestStyle, indicator = { Box(Modifier.size(20.dp).border(2.dp, Color.White)) })
            }
            ToggleRow("Checkbox on") {
                Checkbox(checked = true, onCheckedChange = {
                }, style = visualTestStyle, indicator = { Box(Modifier.size(20.dp).background(Color(0xFF43A047))) { Text("✓") } })
            }
            ToggleRow("Checkbox indeterminate") {
                TriStateCheckbox(ToggleableState.Indeterminate, {
                }, style = visualTestStyle, indicator = { Box(Modifier.size(20.dp).background(Color(0xFFFFA000))) { Text("–") } })
            }
            ToggleRow("Checkbox disabled") {
                Checkbox(true, {
                }, enabled = false, style = visualTestStyle, indicator = {
                    Box(
                        Modifier.size(20.dp).background(Color(0xFF607D8B)),
                    ) { Text("✓") }
                })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(false, {
                }, style = visualTestStyle, indicator = {
                    Box(
                        Modifier.size(20.dp).border(2.dp, Color.White, RoundedCornerShape(10.dp)),
                    )
                })
                RadioButton(true, {
                }, style = visualTestStyle, indicator = {
                    Box(
                        Modifier.size(20.dp).background(Color(0xFF43A047), RoundedCornerShape(10.dp)),
                    )
                })
                Switch(false, {}, style = visualTestStyle) { SwitchIndicator(it) }
                Switch(true, {}, style = visualTestStyle) { SwitchIndicator(it) }
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
        Text(label)
    }
}

@Composable
private fun SwitchIndicator(checked: Boolean) {
    Box(
        Modifier
            .width(44.dp)
            .height(24.dp)
            .background(if (checked) Color(0xFF2E7D32) else Color(0xFF5F6B76), RoundedCornerShape(12.dp)),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Spacer(Modifier.padding(3.dp).size(18.dp).background(Color.White, RoundedCornerShape(9.dp)))
    }
}
