// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.checkbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckboxDefaultsTest {
    @Test
    fun defaultStyleMatchesStyleDefaultsNone() {
        assertEquals(StyleDefaults.None, CheckboxDefaults.style())
    }

    @Test
    fun customParametersAreForwarded() {
        val border = Border(width = 2.dp, color = Color.Blue)
        val style =
            CheckboxDefaults.style(
                colors = StyleDefaults.colors(backgroundColor = Color.Red),
                borders = StyleDefaults.borders(border = border),
                scale = StyleDefaults.scale(focusedScale = 1.2f),
                shapes = StyleDefaults.shapes(shape = RectangleShape),
                alpha = StyleDefaults.alpha(disabledAlpha = 0.3f),
            )

        assertEquals(Color.Red, style.colors.backgroundColor)
        assertEquals(border, style.borders.border)
        assertEquals(1.2f, style.scale.focusedScale)
        assertEquals(RectangleShape, style.shapes.shape)
        assertEquals(0.3f, style.alpha.disabledAlpha)
    }
}
