// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.daio.wild.content.LocalContentColor

/**
 * Basic text component that integrates with Wild's content color system.
 *
 * By default uses [LocalContentColor] as the text color, allowing it to automatically
 * adapt when placed inside styled containers.
 *
 * @param text The text to display.
 * @param modifier Modifier to apply to the text.
 * @param color The color of the text. Defaults to [LocalContentColor].
 * @param style The text style to apply.
 * @param textAlign The text alignment.
 * @param overflow How visual overflow should be handled.
 * @param maxLines Maximum number of lines for the text to span.
 * @param minLines Minimum number of lines for the text to span.
 * @param onTextLayout Callback invoked when the text layout is calculated.
 *
 * @since 0.6.0
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextDefaults.overflow,
    maxLines: Int = TextDefaults.maxLines,
    minLines: Int = TextDefaults.minLines,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    val resolvedColor = if (color == Color.Unspecified) LocalContentColor.current else color
    val mergedStyle =
        style.merge(
            TextStyle(
                color = resolvedColor,
                textAlign = textAlign ?: TextAlign.Unspecified,
            ),
        )

    BasicText(
        text = text,
        modifier = modifier,
        style = mergedStyle,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout ?: {},
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
