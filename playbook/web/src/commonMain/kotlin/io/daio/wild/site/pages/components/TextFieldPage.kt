// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.pages.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.daio.wild.components.text.Text
import io.daio.wild.components.text.TextArea
import io.daio.wild.components.text.TextField
import io.daio.wild.container.Container
import io.daio.wild.site.components.ComponentPage
import io.daio.wild.site.components.ComponentPageData
import io.daio.wild.site.components.Demo
import io.daio.wild.site.components.Platform
import io.daio.wild.site.components.Prop
import io.daio.wild.site.theme.SiteTheme
import io.daio.wild.style.Border

@Composable
fun TextFieldPage(
    modifier: Modifier = Modifier,
    data: ComponentPageData = TextFieldPageDefaults.data,
) {
    ComponentPage(
        modifier = modifier,
        data = data,
    )
}

object TextFieldPageDefaults {
    val data =
        ComponentPageData(
            name = "TextField",
            description =
                "An unstyled, state-based text input primitive built on Compose Foundation. " +
                    "TextArea is the same editor configured for multiline input. Supply visuals " +
                    "through a decorator — Wild does not impose Material chrome.",
            module = "io.daio.wild.components:text",
            demos =
                listOf(
                    Demo("Plain", "Bare TextField with programmatic state.") {
                        val state = rememberTextFieldState("Hello, Wild!")
                        TextField(
                            state = state,
                            modifier = Modifier.width(280.dp),
                            textStyle =
                                SiteTheme.typography.body.copy(
                                    color = SiteTheme.colors.textPrimary,
                                ),
                            cursorBrush = SolidColor(SiteTheme.colors.accent),
                            lineLimits = TextFieldLineLimits.SingleLine,
                        )
                    },
                    Demo("Decorated", "Decorator owns border, padding, and placeholder.") {
                        val state = rememberTextFieldState()
                        Container(
                            modifier = Modifier.width(280.dp).heightIn(min = 40.dp),
                            color = SiteTheme.colors.surface,
                            shape = RoundedCornerShape(SiteTheme.spacing.s),
                            border =
                                Border(
                                    borderStroke = BorderStroke(1.dp, SiteTheme.colors.border),
                                    shape = RoundedCornerShape(SiteTheme.spacing.s),
                                ),
                        ) {
                            TextField(
                                state = state,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                textStyle =
                                    SiteTheme.typography.body.copy(
                                        color = SiteTheme.colors.textPrimary,
                                    ),
                                cursorBrush = SolidColor(SiteTheme.colors.accent),
                                lineLimits = TextFieldLineLimits.SingleLine,
                                decorator =
                                    TextFieldDecorator { innerTextField ->
                                        Box {
                                            if (state.text.isEmpty()) {
                                                Text(
                                                    text = "Placeholder",
                                                    style = SiteTheme.typography.body,
                                                    color = SiteTheme.colors.textSecondary,
                                                )
                                            }
                                            innerTextField()
                                        }
                                    },
                            )
                        }
                    },
                    Demo("Transformed", "Input max length with bracketed output.") {
                        val state = rememberTextFieldState()
                        TextField(
                            state = state,
                            modifier = Modifier.width(280.dp),
                            textStyle =
                                SiteTheme.typography.body.copy(
                                    color = SiteTheme.colors.textPrimary,
                                ),
                            cursorBrush = SolidColor(SiteTheme.colors.accent),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            inputTransformation = InputTransformation.maxLength(8),
                            outputTransformation =
                                OutputTransformation {
                                    if (length > 0) insert(0, "[")
                                    if (length > 0) insert(length, "]")
                                },
                        )
                    },
                    Demo("Disabled", "Rejects input and exposes disabled semantics.") {
                        TextField(
                            state = rememberTextFieldState("Cannot edit"),
                            enabled = false,
                            modifier = Modifier.width(280.dp),
                            textStyle =
                                SiteTheme.typography.body.copy(
                                    color = SiteTheme.colors.textSecondary,
                                ),
                            cursorBrush = SolidColor(SiteTheme.colors.accent),
                            lineLimits = TextFieldLineLimits.SingleLine,
                        )
                    },
                    Demo("Read-only", "Focusable and selectable, but not editable.") {
                        TextField(
                            state = rememberTextFieldState("Read only value"),
                            readOnly = true,
                            modifier = Modifier.width(280.dp),
                            textStyle =
                                SiteTheme.typography.body.copy(
                                    color = SiteTheme.colors.textPrimary,
                                ),
                            cursorBrush = SolidColor(SiteTheme.colors.accent),
                            lineLimits = TextFieldLineLimits.SingleLine,
                        )
                    },
                    Demo("Multiline", "TextArea with a three-line minimum.") {
                        val state = rememberTextFieldState("Line one\nLine two")
                        TextArea(
                            state = state,
                            modifier = Modifier.width(280.dp),
                            textStyle =
                                SiteTheme.typography.body.copy(
                                    color = SiteTheme.colors.textPrimary,
                                ),
                            cursorBrush = SolidColor(SiteTheme.colors.accent),
                        )
                    },
                ),
            usage =
                """
                val state = rememberTextFieldState()
                TextField(
                    state = state,
                    textStyle = LocalTextStyle.current,
                    decorator = TextFieldDecorator { innerTextField ->
                        // Call innerTextField exactly once.
                        innerTextField()
                    },
                )

                TextArea(
                    state = rememberTextFieldState(),
                    minLines = 3,
                    maxLines = 6,
                )
                """.trimIndent(),
            props =
                listOf(
                    Prop("state", "TextFieldState", required = true),
                    Prop("modifier", "Modifier", default = "Modifier"),
                    Prop("enabled", "Boolean", default = "true"),
                    Prop("readOnly", "Boolean", default = "false"),
                    Prop("textStyle", "TextStyle", default = "LocalTextStyle.current"),
                    Prop("inputTransformation", "InputTransformation?", default = "null"),
                    Prop("outputTransformation", "OutputTransformation?", default = "null"),
                    Prop("keyboardOptions", "KeyboardOptions", default = "KeyboardOptions.Default"),
                    Prop("onKeyboardAction", "KeyboardActionHandler?", default = "null"),
                    Prop("lineLimits", "TextFieldLineLimits", default = "TextFieldLineLimits.Default"),
                    Prop("onTextLayout", "(Density.(()->TextLayoutResult?)->Unit)?", default = "null"),
                    Prop("scrollState", "ScrollState", default = "rememberScrollState()"),
                    Prop("cursorBrush", "Brush", default = "SolidColor(LocalContentColor.current)"),
                    Prop("interactionSource", "MutableInteractionSource?", default = "null"),
                    Prop("decorator", "TextFieldDecorator?", default = "null"),
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
