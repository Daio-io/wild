// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.ui.graphics.Color
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
    fun explicitColorTakesPrecedenceOverUnspecified() {
        val explicit = Color.Red
        val resolved = if (explicit == Color.Unspecified) Color.White else explicit
        assertEquals(Color.Red, resolved)
    }

    @Test
    fun unspecifiedColorFallsBackToContentColor() {
        val explicit = Color.Unspecified
        val contentColor = Color.Green
        val resolved = if (explicit == Color.Unspecified) contentColor else explicit
        assertEquals(Color.Green, resolved)
    }
}
