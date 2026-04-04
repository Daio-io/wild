// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.daio.wild.components.button.Button
import io.daio.wild.components.text.Text
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun ButtonPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = ButtonPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object ButtonPageDefaults {
    val data =
        ComponentPageData(
            name = "Button",
            description =
                "An interactive button primitive with click, long-click, and double-click " +
                    "support. Applies style-based visual feedback for hover, focus, and press states.",
            module = "io.daio.wild:button",
            demos =
                listOf(
                    Demo("Basic", "A simple button with default styling.") {
                        Button(
                            onClick = {},
                            style =
                                StyleDefaults.style(
                                    colors =
                                        StyleDefaults.colors(
                                            backgroundColor = Color.White,
                                            contentColor = Color.Black,
                                            // focused = hovered on web
                                            focusedBackgroundColor = Color.Black,
                                            focusedContentColor = Color.White,
                                            hoveredBackgroundColor = Color.Black,
                                            hoveredContentColor = Color.White,
                                            pressedBackgroundColor = SiteTheme.colors.accentSubtle,
                                            // selected = normal
                                            selectedBackgroundColor = Color.White,
                                            selectedContentColor = Color.Black,
                                            // focusSelected = focused
                                            focusedSelectedBackgroundColor = Color.Black,
                                            focusedSelectedContentColor = Color.White,
                                            hoveredSelectedBackgroundColor = Color.Black,
                                            hoveredSelectedContentColor = Color.White,
                                        ),
                                    shapes =
                                        StyleDefaults.shapes(
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                    scale =
                                        StyleDefaults.scale(
                                            pressedScale = 0.95f,
                                        ),
                                ),
                        ) {
                            Text(
                                text = "Click me",
                                modifier = Modifier.padding(horizontal = SiteTheme.spacing.m, vertical = SiteTheme.spacing.s),
                            )
                        }
                    },
                ),
            usage =
                """
                Button(
                    onClick = { /* handle click */ },
                    style = StyleDefaults.style(
                        colors = StyleDefaults.colors(
                            backgroundColor = MyTheme.colors.primary,
                            contentColor = MyTheme.colors.onPrimary,
                        ),
                        shapes = StyleDefaults.shapes(
                            shape = RoundedCornerShape(8.dp),
                        ),
                    ),
                ) {
                    Text("Click me")
                }
                """.trimIndent(),
            props =
                listOf(
                    Prop("onClick", "() -> Unit", required = true),
                    Prop("content", "@Composable BoxScope.() -> Unit", required = true),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("onLongClick", "(() -> Unit)?", default = "null"),
                    Prop("onDoubleClick", "(() -> Unit)?", default = "null"),
                    Prop("style", "Style", default = "ButtonDefaults.style()"),
                    Prop("contentPadding", "PaddingValues", default = "ButtonDefaults.contentPadding"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop("modifier", "Modifier", default = "Modifier"),
                ),
            platforms =
                listOf(
                    Platform.Android,
                    Platform.AndroidTV,
                    Platform.Desktop,
                    Platform.MacOS,
                    Platform.Web,
                    Platform.IOS,
                ),
        )
}
