// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.textfield

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import io.daio.wild.components.text.LocalTextStyle
import io.daio.wild.content.LocalContentColor

/**
 * An unstyled, state-based text field primitive.
 *
 * Wraps Compose Foundation's state-based [BasicTextField] and resolves text color from
 * [textStyle] or [LocalContentColor]. Does not impose background, border, padding, minimum
 * size, label, placeholder, or error visuals — supply those through [decorator] if needed.
 *
 * A [decorator] must invoke its supplied inner field exactly once.
 *
 * When [LocalContentColor] is [androidx.compose.ui.graphics.Color.Unspecified], the default
 * cursor brush is also unspecified. Callers should provide an explicit [cursorBrush] in that
 * case.
 *
 * Example:
 * ```
 * val state = rememberTextFieldState()
 * TextField(
 *     state = state,
 *     decorator = TextFieldDecorator { innerTextField ->
 *         Box(Modifier.padding(8.dp)) {
 *             innerTextField()
 *         }
 *     },
 * )
 * ```
 *
 * @param state The state that owns the field's text, selection, and composition.
 * @param modifier Modifier to apply to the field.
 * @param enabled Whether the field accepts focus and user input.
 * @param readOnly Whether the field can be focused and selected without accepting edits.
 * @param textStyle The style used to render the field text.
 * @param inputTransformation Transformation applied to user edits.
 * @param outputTransformation Transformation applied when the field is rendered.
 * @param keyboardOptions Options for the software keyboard.
 * @param onKeyboardAction Callback for software keyboard actions.
 * @param lineLimits The single-line or multiline constraints for the field.
 * @param onTextLayout Callback invoked when text layout is calculated.
 * @param scrollState Scroll state used by the field.
 * @param cursorBrush Brush used to draw the cursor.
 * @param interactionSource Optional source for focus and press interactions. Forwarded as-is;
 *   a second source is not created for decoration.
 * @param decorator Optional visual decoration around the inner field.
 *
 * @since 0.6.0
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    cursorBrush: Brush = SolidColor(LocalContentColor.current),
    interactionSource: MutableInteractionSource? = null,
    decorator: TextFieldDecorator? = null,
) {
    val resolvedTextStyle =
        textStyle.copy(color = textStyle.color.takeOrElse { LocalContentColor.current })

    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        textStyle = resolvedTextStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout ?: {},
        scrollState = scrollState,
        cursorBrush = cursorBrush,
        interactionSource = interactionSource,
        decorator = decorator,
    )
}

/**
 * A multiline configuration of [TextField].
 *
 * Exposes the same parameters as [TextField] except [TextField]'s `lineLimits`, which are
 * derived from [minLines] and [maxLines].
 *
 * Example:
 * ```
 * val state = rememberTextFieldState()
 * TextArea(
 *     state = state,
 *     minLines = 3,
 *     maxLines = 6,
 * )
 * ```
 *
 * @param state The state that owns the area's text, selection, and composition.
 * @param modifier Modifier to apply to the area.
 * @param enabled Whether the area accepts focus and user input.
 * @param readOnly Whether the area can be focused and selected without accepting edits.
 * @param textStyle The style used to render the area text.
 * @param inputTransformation Transformation applied to user edits.
 * @param outputTransformation Transformation applied when the area is rendered.
 * @param keyboardOptions Options for the software keyboard.
 * @param onKeyboardAction Callback for software keyboard actions.
 * @param minLines Minimum number of visible text lines. Must be at least 1.
 * @param maxLines Maximum number of visible text lines. Must be greater than or equal to
 *   [minLines].
 * @param onTextLayout Callback invoked when text layout is calculated.
 * @param scrollState Scroll state used by the area.
 * @param cursorBrush Brush used to draw the cursor.
 * @param interactionSource Optional source for focus and press interactions.
 * @param decorator Optional visual decoration around the inner field.
 *
 * @since 0.6.0
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextArea(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    minLines: Int = TextFieldDefaults.textAreaMinLines,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
    cursorBrush: Brush = SolidColor(LocalContentColor.current),
    interactionSource: MutableInteractionSource? = null,
    decorator: TextFieldDecorator? = null,
) {
    require(minLines >= 1) { "minLines must be at least 1" }
    require(maxLines >= minLines) { "maxLines must be greater than or equal to minLines" }

    TextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits =
            TextFieldLineLimits.MultiLine(
                minHeightInLines = minLines,
                maxHeightInLines = maxLines,
            ),
        onTextLayout = onTextLayout,
        scrollState = scrollState,
        cursorBrush = cursorBrush,
        interactionSource = interactionSource,
        decorator = decorator,
    )
}

/**
 * Default values used by [TextField] and [TextArea].
 *
 * @since 0.6.0
 */
object TextFieldDefaults {
    /** The default minimum number of visible lines in a [TextArea]. */
    val textAreaMinLines: Int = 3
}
