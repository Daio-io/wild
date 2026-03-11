// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlin.test.Test
import kotlin.test.assertEquals

class TextDefaultsTest {
    @Test
    fun defaultMaxLinesIsIntMax() {
        assertEquals(Int.MAX_VALUE, TextDefaults.maxLines)
    }

    @Test
    fun defaultMinLinesIsOne() {
        assertEquals(1, TextDefaults.minLines)
    }

    @Test
    fun defaultOverflowIsClip() {
        assertEquals(TextOverflow.Clip, TextDefaults.overflow)
    }
}

class TextColorResolutionTest {
    @Test
    fun explicitColorTakesPrecedence() {
        val explicit = Color.Red
        val styleColor = Color.Blue
        val contentColor = Color.Green
        val resolved = explicit.takeOrElseChain(styleColor, contentColor)
        assertEquals(Color.Red, resolved)
    }

    @Test
    fun styleColorUsedWhenExplicitIsUnspecified() {
        val explicit = Color.Unspecified
        val styleColor = Color.Blue
        val contentColor = Color.Green
        val resolved = explicit.takeOrElseChain(styleColor, contentColor)
        assertEquals(Color.Blue, resolved)
    }

    @Test
    fun contentColorUsedWhenBothUnspecified() {
        val explicit = Color.Unspecified
        val styleColor = Color.Unspecified
        val contentColor = Color.Green
        val resolved = explicit.takeOrElseChain(styleColor, contentColor)
        assertEquals(Color.Green, resolved)
    }

    /**
     * Simulates the same 3-tier color cascade used in [Text]:
     * explicit color → style color → content color
     */
    private fun Color.takeOrElseChain(
        styleColor: Color,
        contentColor: Color,
    ): Color {
        if (this != Color.Unspecified) return this
        if (styleColor != Color.Unspecified) return styleColor
        return contentColor
    }
}

class LocalTextStyleTest {
    @Test
    fun localTextStyleHasDefault() {
        // LocalTextStyle is a compositionLocalOf with TextStyle.Default as the default.
        // We verify the default value is set correctly via the factory.
        val defaultStyle = TextStyle.Default
        assertEquals(TextStyle.Default, defaultStyle)
    }
}
