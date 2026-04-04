// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.container.Container
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.StyleDefaults

@Composable
fun ContainerPage(modifier: Modifier = Modifier) {
    ComponentPage(
        modifier = modifier,
        data =
            ComponentPageData(
                name = "Container",
                description =
                    "A foundational layout component. The static variant provides color, shape, " +
                        "and border styling. The interactive variant adds click handling with " +
                        "state-based visual feedback for hover, focus, press, and selection.",
                module = "io.daio.wild:container",
                demos =
                    listOf(
                        Demo("Static", "Containers with different colors and shapes.") {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Container(
                                    modifier = Modifier.size(80.dp),
                                    color = SiteTheme.colors.accent,
                                    contentColor = SiteTheme.colors.background,
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Card")
                                    }
                                }
                                Container(
                                    modifier = Modifier.size(80.dp),
                                    color = SiteTheme.colors.surface,
                                    contentColor = SiteTheme.colors.textPrimary,
                                    shape = CircleShape,
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "Circle")
                                    }
                                }
                            }
                        },
                        Demo(
                            "Interactive",
                            "Hover over this container to see state-based styling.",
                        ) {
                            Container(
                                onClick = {},
                                modifier = Modifier.size(140.dp, 60.dp),
                                style =
                                    StyleDefaults.style(
                                        colors =
                                            StyleDefaults.colors(
                                                backgroundColor = SiteTheme.colors.surface,
                                                contentColor = SiteTheme.colors.textPrimary,
                                                hoveredBackgroundColor = SiteTheme.colors.accent,
                                                hoveredContentColor = SiteTheme.colors.background,
                                            ),
                                        shapes =
                                            StyleDefaults.shapes(
                                                shape = RoundedCornerShape(8.dp),
                                            ),
                                    ),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = "Hover me")
                                }
                            }
                        },
                    ),
                usage =
                    """
                    // Static container
                    Container(
                        color = MyTheme.colors.surface,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Content")
                    }

                    // Interactive container
                    Container(
                        onClick = { /* handle click */ },
                        style = StyleDefaults.style(
                            colors = StyleDefaults.colors(
                                backgroundColor = MyTheme.colors.surface,
                                hoveredBackgroundColor = MyTheme.colors.accent,
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
                        Prop("content", "@Composable BoxScope.() -> Unit", required = true),
                        Prop("modifier", "Modifier", default = "Modifier"),
                        Prop("color", "Color", default = "Color.Unspecified"),
                        Prop("contentColor", "Color", default = "Color.Unspecified"),
                        Prop("shape", "Shape", default = "RectangleShape"),
                        Prop("border", "Border", default = "BorderDefaults.None"),
                        Prop("onClick", "() -> Unit"),
                        Prop("enabled", "Boolean", default = "true"),
                        Prop("onLongClick", "(() -> Unit)?", default = "null"),
                        Prop("onDoubleClick", "(() -> Unit)?", default = "null"),
                        Prop("style", "Style", default = "StyleDefaults.None"),
                        Prop("selected", "Boolean?", default = "null"),
                        Prop("interactionSource", "MutableInteractionSource?", default = "null"),
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
            ),
    )
}
