// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import io.daio.wild.content.LocalContentColor

/**
 * Basic text component that integrates with Wild's content color system.
 *
 * The default [style] uses the [LocalTextStyle] provided by parent components. If you are
 * setting your own style, consider first retrieving [LocalTextStyle] and using [TextStyle.copy]
 * to keep any inherited attributes, only modifying the specific attributes you want to override.
 *
 * For ease of use, commonly used parameters from [TextStyle] are also present here. The order of
 * precedence is:
 * - If a parameter is explicitly set here (i.e., it is not `null` or [TextUnit.Unspecified]),
 *   then this parameter will always be used.
 * - If a parameter is not set, then the corresponding value from [style] will be used instead.
 *
 * For [color], if [color] is not set, and [style] does not have a color, then
 * [LocalContentColor] will be used.
 *
 * @param text The text to display.
 * @param modifier Modifier to apply to the text.
 * @param color The color of the text. If [Color.Unspecified], and [style] has no color set,
 *   this will be [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic).
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to be used when rendering the text.
 * @param letterSpacing The amount of space to add between each letter.
 * @param textDecoration The decorations to paint on the text (e.g., an underline).
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * @param lineHeight Line height for the text in [TextUnit] unit, e.g. SP or EM.
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param maxLines Maximum number of lines for the text to span.
 * @param minLines Minimum number of lines for the text to span.
 * @param onTextLayout Callback invoked when the text layout is calculated.
 * @param style Style configuration for the text such as color, font, line height etc.
 *
 * @since 0.6.0
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextDefaults.overflow,
    softWrap: Boolean = true,
    maxLines: Int = TextDefaults.maxLines,
    minLines: Int = TextDefaults.minLines,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }

    BasicText(
        text = text,
        modifier = modifier,
        style =
            style.merge(
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign ?: TextAlign.Unspecified,
                lineHeight = lineHeight,
            ),
        onTextLayout = onTextLayout ?: {},
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * Basic text component that displays [AnnotatedString] content with support for
 * inline styling, spans, and links.
 *
 * Integrates with Wild's content color system via [LocalContentColor] and supports
 * typography inheritance via [LocalTextStyle].
 *
 * @param text The [AnnotatedString] to display.
 * @param modifier Modifier to apply to the text.
 * @param color The color of the text. If [Color.Unspecified], and [style] has no color set,
 *   this will be [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text.
 * @param fontStyle The typeface variant to use when drawing the letters.
 * @param fontWeight The typeface thickness to use when painting the text.
 * @param fontFamily The font family to be used when rendering the text.
 * @param letterSpacing The amount of space to add between each letter.
 * @param textDecoration The decorations to paint on the text.
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * @param lineHeight Line height for the text.
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param maxLines Maximum number of lines for the text to span.
 * @param minLines Minimum number of lines for the text to span.
 * @param onTextLayout Callback invoked when the text layout is calculated.
 * @param style Style configuration for the text.
 *
 * @since 0.6.0
 */
@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextDefaults.overflow,
    softWrap: Boolean = true,
    maxLines: Int = TextDefaults.maxLines,
    minLines: Int = TextDefaults.minLines,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }

    BasicText(
        text = text,
        modifier = modifier,
        style =
            style.merge(
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontStyle = fontStyle,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign ?: TextAlign.Unspecified,
                lineHeight = lineHeight,
            ),
        onTextLayout = onTextLayout ?: {},
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/**
 * Contains the default values used by [Text].
 *
 * @since 0.6.0
 */
object TextDefaults {
    /**
     * The default maximum number of lines.
     */
    val maxLines: Int = Int.MAX_VALUE

    /**
     * The default minimum number of lines.
     */
    val minLines: Int = 1

    /**
     * The default text overflow behavior.
     */
    val overflow: TextOverflow = TextOverflow.Clip
}
