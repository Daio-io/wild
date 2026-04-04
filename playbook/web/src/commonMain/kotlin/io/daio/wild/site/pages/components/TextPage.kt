// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.daio.wild.components.text.Text
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme

@Composable
fun TextPage(modifier: Modifier = Modifier) {
    ComponentPage(
        modifier = modifier,
        data =
            ComponentPageData(
                name = "Text",
                description =
                    "A text component that integrates with LocalContentColor and LocalTextStyle " +
                        "composition locals for seamless theming. Inherits color from the content " +
                        "color local by default.",
                module = "io.daio.wild.components:text",
                demos =
                    listOf(
                        Demo("Typography Scale", "Text at different sizes and weights.") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Heading Large",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SiteTheme.colors.textPrimary,
                                )
                                Text(
                                    text = "Heading Medium",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SiteTheme.colors.textPrimary,
                                )
                                Text(
                                    text = "Body text - the quick brown fox jumps over the lazy dog.",
                                    fontSize = 14.sp,
                                    color = SiteTheme.colors.textPrimary,
                                )
                                Text(
                                    text = "Caption / secondary text",
                                    fontSize = 12.sp,
                                    color = SiteTheme.colors.textSecondary,
                                )
                                Text(
                                    text = "Italic text for emphasis",
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = SiteTheme.colors.textPrimary,
                                )
                                Text(
                                    text = "Accent colored text",
                                    fontSize = 14.sp,
                                    color = SiteTheme.colors.accent,
                                )
                            }
                        },
                    ),
                usage =
                    """
                    // Basic usage - inherits color from LocalContentColor
                    Text("Hello, Wild!")

                    // With explicit styling
                    Text(
                        text = "Styled text",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTheme.colors.primary,
                    )
                    """.trimIndent(),
                props =
                    listOf(
                        Prop("text", "String", required = true),
                        Prop("modifier", "Modifier", default = "Modifier"),
                        Prop("color", "Color", default = "Color.Unspecified"),
                        Prop("fontSize", "TextUnit", default = "TextUnit.Unspecified"),
                        Prop("fontStyle", "FontStyle?", default = "null"),
                        Prop("fontWeight", "FontWeight?", default = "null"),
                        Prop("fontFamily", "FontFamily?", default = "null"),
                        Prop("letterSpacing", "TextUnit", default = "TextUnit.Unspecified"),
                        Prop("textDecoration", "TextDecoration?", default = "null"),
                        Prop("textAlign", "TextAlign?", default = "null"),
                        Prop("lineHeight", "TextUnit", default = "TextUnit.Unspecified"),
                        Prop("overflow", "TextOverflow", default = "TextDefaults.overflow"),
                        Prop("softWrap", "Boolean", default = "true"),
                        Prop("maxLines", "Int", default = "TextDefaults.maxLines"),
                        Prop("minLines", "Int", default = "TextDefaults.minLines"),
                        Prop("onTextLayout", "((TextLayoutResult) -> Unit)?", default = "null"),
                        Prop("style", "TextStyle", default = "LocalTextStyle.current"),
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
