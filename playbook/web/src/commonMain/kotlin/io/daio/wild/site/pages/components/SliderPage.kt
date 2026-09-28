// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.components.slider.RangeSlider
import io.daio.wild.components.slider.Slider
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme

@Composable
fun SliderPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = SliderPageDefaults.data,
) {
    ComponentPage(modifier = modifier, data = data)
}

object SliderPageDefaults {
    val data =
        ComponentPageData(
            name = "Slider",
            description = "Controlled, unstyled single-value and range sliders with caller-owned slots.",
            module = "io.daio.wild.components:slider",
            demos =
                listOf(
                    Demo("Continuous", "A caller-controlled continuous slider.") {
                        var value by remember { mutableStateOf(0.4f) }
                        DemoSlider(value = value, onValueChange = { value = it })
                    },
                    Demo("Discrete", "Discrete values snap to steps + 2 positions.") {
                        var value by remember { mutableStateOf(0.5f) }
                        DemoSlider(value = value, steps = 3, onValueChange = { value = it })
                    },
                    Demo("Range and overlapping thumbs", "Both thumbs are independently controlled.") {
                        var value by remember { mutableStateOf(0.35f..0.65f) }
                        RangeSlider(
                            value = value,
                            onValueChange = { value = it },
                            modifier = Modifier.fillMaxWidth(),
                            startThumb = { Box(Modifier.size(18.dp).background(SiteTheme.colors.accent)) },
                            endThumb = { Box(Modifier.size(18.dp).background(SiteTheme.colors.accent)) },
                            track = { Box(Modifier.fillMaxWidth().height(4.dp).background(Color.LightGray)) },
                        )
                    },
                    Demo("Disabled", "Disabled controls retain their value and semantics.") {
                        DemoSlider(value = 0.6f, enabled = false, onValueChange = {})
                    },
                ),
            usage =
                """
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    thumb = { /* draw from fraction */ },
                    track = { /* draw from fraction */ },
                )
                """.trimIndent(),
            props =
                listOf(
                    Prop("value", "Float", required = true),
                    Prop("onValueChange", "(Float) -> Unit", required = true),
                    Prop("valueRange", "ClosedFloatingPointRange<Float>", default = "0f..1f"),
                    Prop("steps", "Int", default = "0"),
                    Prop("thumb / track", "slot scopes", required = true),
                ),
            platforms = listOf(Platform.Android, Platform.AndroidTV, Platform.Desktop, Platform.Web),
        )
}

@Composable
private fun DemoSlider(
    value: Float,
    enabled: Boolean = true,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        steps = steps,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        thumb = { Box(Modifier.size(18.dp).background(SiteTheme.colors.accent)) },
        track = { Box(Modifier.fillMaxWidth().height(4.dp).background(Color.LightGray)) },
    )
}
