// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.daio.wild.style.Border
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ToggleableDefaultsTest {
    @Test
    fun defaultStyleMatchesStyleDefaultsNone() {
        assertEquals(StyleDefaults.None, ToggleableDefaults.style())
    }

    @Test
    fun customColorsAreForwarded() {
        val colors = StyleDefaults.colors(backgroundColor = Color.Red)
        val style = ToggleableDefaults.style(colors = colors)
        assertEquals(Color.Red, style.colors.backgroundColor)
    }

    @Test
    fun customBordersAreForwarded() {
        val border = Border(width = 2.dp, color = Color.Blue)
        val borders = StyleDefaults.borders(border = border)
        val style = ToggleableDefaults.style(borders = borders)
        assertEquals(border, style.borders.border)
    }

    @Test
    fun customScaleIsForwarded() {
        val scale = StyleDefaults.scale(focusedScale = 1.5f)
        val style = ToggleableDefaults.style(scale = scale)
        assertEquals(1.5f, style.scale.focusedScale)
    }

    @Test
    fun customShapesAreForwarded() {
        val shapes = StyleDefaults.shapes(shape = RectangleShape)
        val style = ToggleableDefaults.style(shapes = shapes)
        assertEquals(RectangleShape, style.shapes.shape)
    }

    @Test
    fun customAlphaIsForwarded() {
        val alpha = StyleDefaults.alpha(disabledAlpha = 0.2f)
        val style = ToggleableDefaults.style(alpha = alpha)
        assertEquals(0.2f, style.alpha.disabledAlpha)
    }

    @Test
    fun selectedColorsApplyForCheckedState() {
        val colors = StyleDefaults.colors(
            backgroundColor = Color.Gray,
            selectedBackgroundColor = Color.Green,
        )
        val style = ToggleableDefaults.style(colors = colors)
        assertEquals(Color.Gray, style.colors.backgroundColor)
        assertEquals(Color.Green, style.colors.selectedBackgroundColor)
        assertNotEquals(style.colors.backgroundColor, style.colors.selectedBackgroundColor)
    }

    @Test
    fun allParametersCombine() {
        val colors = StyleDefaults.colors(backgroundColor = Color.Red)
        val border = Border(width = 2.dp, color = Color.Blue)
        val borders = StyleDefaults.borders(border = border)
        val scale = StyleDefaults.scale(focusedScale = 1.2f)
        val shapes = StyleDefaults.shapes(shape = RectangleShape)
        val alpha = StyleDefaults.alpha(disabledAlpha = 0.3f)

        val style = ToggleableDefaults.style(
            colors = colors,
            borders = borders,
            scale = scale,
            shapes = shapes,
            alpha = alpha,
        )

        assertEquals(Color.Red, style.colors.backgroundColor)
        assertEquals(border, style.borders.border)
        assertEquals(1.2f, style.scale.focusedScale)
        assertEquals(RectangleShape, style.shapes.shape)
        assertEquals(0.3f, style.alpha.disabledAlpha)
    }
}

class ToggleableStateTest {
    @Test
    fun checkedMapsToToggleableStateOn() {
        assertEquals(ToggleableState.On, ToggleableState(true))
    }

    @Test
    fun uncheckedMapsToToggleableStateOff() {
        assertEquals(ToggleableState.Off, ToggleableState(false))
    }

    @Test
    fun toggleableStatesAreDistinct() {
        assertNotEquals(ToggleableState(true), ToggleableState(false))
    }

    @Test
    fun indeterminateStateExists() {
        // Ensure Indeterminate is a valid state (relevant for future tri-state checkbox support)
        assertNotEquals(ToggleableState.Indeterminate, ToggleableState.On)
        assertNotEquals(ToggleableState.Indeterminate, ToggleableState.Off)
    }
}
