// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class StyleDefaultsTest {
    @Test
    fun defaultFactoriesReturnCachedNoneProperties() {
        assertSame(StyleDefaults.None.colors, StyleDefaults.colors())
        assertSame(StyleDefaults.None.borders, StyleDefaults.borders())
        assertSame(StyleDefaults.None.scale, StyleDefaults.scale())
        assertSame(StyleDefaults.None.shapes, StyleDefaults.shapes())
        assertSame(StyleDefaults.None.alpha, StyleDefaults.alpha())
        assertSame(StyleDefaults.None, StyleDefaults.style())
        assertSame(
            StyleDefaults.None,
            StyleDefaults.style(
                colors = StyleDefaults.colors(),
                borders = StyleDefaults.borders(),
                scale = StyleDefaults.scale(),
                shapes = StyleDefaults.shapes(),
                alpha = StyleDefaults.alpha(),
            ),
        )
    }

    @Test
    fun defaultValuesRemainUnchanged() {
        val colors = StyleDefaults.colors()
        assertEquals(
            Colors(
                backgroundColor = Color.Black,
                focusedBackgroundColor = Color.Black,
                pressedBackgroundColor = Color.Black,
                hoveredBackgroundColor = Color.Black,
                disabledBackgroundColor = Color.Black,
                selectedBackgroundColor = Color.Black,
                focusedSelectedBackgroundColor = Color.Black,
                pressedSelectedBackgroundColor = Color.Black,
                hoveredSelectedBackgroundColor = Color.Black,
                focusedDisabledBackgroundColor = Color.Black,
                pressedDisabledBackgroundColor = Color.Black,
                hoveredDisabledBackgroundColor = Color.Black,
                contentColor = Color.White,
                focusedContentColor = Color.White,
                pressedContentColor = Color.White,
                hoveredContentColor = Color.White,
                disabledContentColor = Color.White,
                selectedContentColor = Color.White,
                focusedSelectedContentColor = Color.White,
                pressedSelectedContentColor = Color.White,
                hoveredSelectedContentColor = Color.White,
                pressedDisabledContentColor = Color.White,
                focusedDisabledContentColor = Color.White,
                hoveredDisabledContentColor = Color.White,
            ),
            colors,
        )
        assertEquals(Color.Black, colors.disabledBackgroundColor)
        assertEquals(Color.White, colors.disabledContentColor)

        val borders = StyleDefaults.borders()
        assertEquals(
            Borders(
                border = BorderDefaults.None,
                focusedBorder = BorderDefaults.None,
                hoveredBorder = BorderDefaults.None,
                pressedBorder = BorderDefaults.None,
                selectedBorder = BorderDefaults.None,
                disabledBorder = BorderDefaults.None,
                focusedSelectedBorder = BorderDefaults.None,
                pressedSelectedBorder = BorderDefaults.None,
                hoveredSelectedBorder = BorderDefaults.None,
                focusedDisabledBorder = BorderDefaults.None,
                pressedDisabledBorder = BorderDefaults.None,
                hoveredDisabledBorder = BorderDefaults.None,
            ),
            borders,
        )
        assertEquals(BorderDefaults.None, borders.border)
        assertEquals(BorderDefaults.None, borders.focusedBorder)
        assertEquals(BorderDefaults.None, borders.hoveredBorder)
        assertEquals(BorderDefaults.None, borders.pressedBorder)
        assertEquals(BorderDefaults.None, borders.selectedBorder)
        assertEquals(BorderDefaults.None, borders.disabledBorder)
        assertEquals(BorderDefaults.None, borders.focusedSelectedBorder)
        assertEquals(BorderDefaults.None, borders.pressedSelectedBorder)
        assertEquals(BorderDefaults.None, borders.hoveredSelectedBorder)
        assertEquals(BorderDefaults.None, borders.focusedDisabledBorder)
        assertEquals(BorderDefaults.None, borders.pressedDisabledBorder)
        assertEquals(BorderDefaults.None, borders.hoveredDisabledBorder)

        val scale = StyleDefaults.scale()
        assertEquals(
            Scale(
                scale = 1f,
                focusedScale = 1f,
                hoveredScale = 1f,
                pressedScale = 1f,
                selectedScale = 1f,
                disabledScale = 1f,
                focusedSelectedScale = 1f,
                pressedSelectedScale = 1f,
                hoveredSelectedScale = 1f,
                focusedDisabledScale = 1f,
                pressedDisabledScale = 1f,
                hoveredDisabledScale = 1f,
                animationSpec = null,
            ),
            scale,
        )
        assertEquals(1f, scale.scale)
        assertEquals(1f, scale.focusedScale)
        assertEquals(1f, scale.hoveredScale)
        assertEquals(1f, scale.pressedScale)
        assertEquals(1f, scale.selectedScale)
        assertEquals(1f, scale.disabledScale)
        assertEquals(1f, scale.focusedSelectedScale)
        assertEquals(1f, scale.pressedSelectedScale)
        assertEquals(1f, scale.hoveredSelectedScale)
        assertEquals(1f, scale.focusedDisabledScale)
        assertEquals(1f, scale.pressedDisabledScale)
        assertEquals(1f, scale.hoveredDisabledScale)
        assertEquals(null, scale.animationSpec)

        val shapes = StyleDefaults.shapes()
        assertEquals(
            Shapes(
                shape = RectangleShape,
                focusedShape = RectangleShape,
                hoveredShape = RectangleShape,
                pressedShape = RectangleShape,
                selectedShape = RectangleShape,
                disabledShape = RectangleShape,
                focusedSelectedShape = RectangleShape,
                pressedSelectedShape = RectangleShape,
                hoveredSelectedShape = RectangleShape,
                focusedDisabledShape = RectangleShape,
                pressedDisabledShape = RectangleShape,
                hoveredDisabledShape = RectangleShape,
            ),
            shapes,
        )
        assertEquals(RectangleShape, shapes.shape)
        assertEquals(RectangleShape, shapes.focusedShape)
        assertEquals(RectangleShape, shapes.hoveredShape)
        assertEquals(RectangleShape, shapes.pressedShape)
        assertEquals(RectangleShape, shapes.selectedShape)
        assertEquals(RectangleShape, shapes.disabledShape)
        assertEquals(RectangleShape, shapes.focusedSelectedShape)
        assertEquals(RectangleShape, shapes.pressedSelectedShape)
        assertEquals(RectangleShape, shapes.hoveredSelectedShape)
        assertEquals(RectangleShape, shapes.focusedDisabledShape)
        assertEquals(RectangleShape, shapes.pressedDisabledShape)
        assertEquals(RectangleShape, shapes.hoveredDisabledShape)

        val alpha = StyleDefaults.alpha()
        assertEquals(
            Alpha(
                alpha = 1f,
                focusedAlpha = 1f,
                hoveredAlpha = 1f,
                pressedAlpha = 1f,
                selectedAlpha = 1f,
                disabledAlpha = 0.6f,
                focusedSelectedAlpha = 1f,
                pressedSelectedAlpha = 1f,
                hoveredSelectedAlpha = 1f,
                focusedDisabledAlpha = 0.6f,
                pressedDisabledAlpha = 0.6f,
                hoveredDisabledAlpha = 0.6f,
            ),
            alpha,
        )
        assertEquals(1f, alpha.alpha)
        assertEquals(1f, alpha.focusedAlpha)
        assertEquals(1f, alpha.hoveredAlpha)
        assertEquals(1f, alpha.pressedAlpha)
        assertEquals(1f, alpha.selectedAlpha)
        assertEquals(1f, alpha.focusedSelectedAlpha)
        assertEquals(1f, alpha.pressedSelectedAlpha)
        assertEquals(1f, alpha.hoveredSelectedAlpha)
        assertEquals(0.6f, alpha.disabledAlpha)
        assertEquals(0.6f, alpha.focusedDisabledAlpha)
        assertEquals(0.6f, alpha.pressedDisabledAlpha)
        assertEquals(0.6f, alpha.hoveredDisabledAlpha)
    }

    @Test
    fun customizedFactoriesDoNotReuseDefaults() {
        val colors = StyleDefaults.colors(backgroundColor = Color.Red)
        assertNotSame(StyleDefaults.None.colors, colors)
        assertEquals(Color.Red, colors.backgroundColor)

        val border = Border(width = 1.dp, color = Color.Red)
        val borders = StyleDefaults.borders(border = border)
        assertNotSame(StyleDefaults.None.borders, borders)
        assertEquals(border, borders.border)

        val scale = StyleDefaults.scale(focusedScale = 1.1f)
        assertNotSame(StyleDefaults.None.scale, scale)
        assertEquals(1.1f, scale.focusedScale)

        val shapes = StyleDefaults.shapes(shape = CircleShape)
        assertNotSame(StyleDefaults.None.shapes, shapes)
        assertEquals(CircleShape, shapes.shape)

        val alpha = StyleDefaults.alpha(disabledAlpha = 0.5f)
        assertNotSame(StyleDefaults.None.alpha, alpha)
        assertEquals(0.5f, alpha.disabledAlpha)
    }

    @Test
    fun customizedFactoriesPreserveDependentDefaults() {
        val colors = StyleDefaults.colors(backgroundColor = Color.Red, contentColor = Color.Green)
        assertEquals(Color.Red, colors.backgroundColor)
        assertEquals(Color.Red, colors.focusedBackgroundColor)
        assertEquals(Color.Red, colors.pressedBackgroundColor)
        assertEquals(Color.Red, colors.hoveredBackgroundColor)
        assertEquals(Color.Red, colors.disabledBackgroundColor)
        assertEquals(Color.Red, colors.selectedBackgroundColor)
        assertEquals(Color.Red, colors.focusedSelectedBackgroundColor)
        assertEquals(Color.Red, colors.pressedSelectedBackgroundColor)
        assertEquals(Color.Red, colors.hoveredSelectedBackgroundColor)
        assertEquals(Color.Red, colors.focusedDisabledBackgroundColor)
        assertEquals(Color.Red, colors.pressedDisabledBackgroundColor)
        assertEquals(Color.Red, colors.hoveredDisabledBackgroundColor)
        assertEquals(Color.Green, colors.contentColor)
        assertEquals(Color.Green, colors.focusedContentColor)
        assertEquals(Color.Green, colors.pressedContentColor)
        assertEquals(Color.Green, colors.hoveredContentColor)
        assertEquals(Color.Green, colors.disabledContentColor)
        assertEquals(Color.Green, colors.selectedContentColor)
        assertEquals(Color.Green, colors.focusedSelectedContentColor)
        assertEquals(Color.Green, colors.pressedSelectedContentColor)
        assertEquals(Color.Green, colors.hoveredSelectedContentColor)
        assertEquals(Color.Green, colors.pressedDisabledContentColor)
        assertEquals(Color.Green, colors.focusedDisabledContentColor)
        assertEquals(Color.Green, colors.hoveredDisabledContentColor)

        val customBorder = Border(width = 1.dp, color = Color.Red)
        val borders = StyleDefaults.borders(border = customBorder)
        assertEquals(customBorder, borders.border)
        assertEquals(customBorder, borders.focusedBorder)
        assertEquals(customBorder, borders.hoveredBorder)
        assertEquals(customBorder, borders.pressedBorder)
        assertEquals(customBorder, borders.selectedBorder)
        assertEquals(customBorder, borders.disabledBorder)
        assertEquals(customBorder, borders.focusedSelectedBorder)
        assertEquals(customBorder, borders.pressedSelectedBorder)
        assertEquals(customBorder, borders.hoveredSelectedBorder)
        assertEquals(customBorder, borders.focusedDisabledBorder)
        assertEquals(customBorder, borders.pressedDisabledBorder)
        assertEquals(customBorder, borders.hoveredDisabledBorder)

        val scale = StyleDefaults.scale(scale = 1.2f)
        assertEquals(1.2f, scale.scale)
        assertEquals(1.2f, scale.focusedScale)
        assertEquals(1.2f, scale.hoveredScale)
        assertEquals(1.2f, scale.pressedScale)
        assertEquals(1.2f, scale.selectedScale)
        assertEquals(1.2f, scale.disabledScale)
        assertEquals(1.2f, scale.focusedSelectedScale)
        assertEquals(1.2f, scale.pressedSelectedScale)
        assertEquals(1.2f, scale.hoveredSelectedScale)
        assertEquals(1.2f, scale.focusedDisabledScale)
        assertEquals(1.2f, scale.pressedDisabledScale)
        assertEquals(1.2f, scale.hoveredDisabledScale)
        assertEquals(null, scale.animationSpec)

        val shapes = StyleDefaults.shapes(shape = CircleShape)
        assertEquals(CircleShape, shapes.shape)
        assertEquals(CircleShape, shapes.focusedShape)
        assertEquals(CircleShape, shapes.hoveredShape)
        assertEquals(CircleShape, shapes.pressedShape)
        assertEquals(CircleShape, shapes.selectedShape)
        assertEquals(CircleShape, shapes.disabledShape)
        assertEquals(CircleShape, shapes.focusedSelectedShape)
        assertEquals(CircleShape, shapes.pressedSelectedShape)
        assertEquals(CircleShape, shapes.hoveredSelectedShape)
        assertEquals(CircleShape, shapes.focusedDisabledShape)
        assertEquals(CircleShape, shapes.pressedDisabledShape)
        assertEquals(CircleShape, shapes.hoveredDisabledShape)

        val alpha = StyleDefaults.alpha(alpha = 0.8f)
        assertEquals(0.8f, alpha.alpha)
        assertEquals(0.8f, alpha.focusedAlpha)
        assertEquals(0.8f, alpha.hoveredAlpha)
        assertEquals(0.8f, alpha.pressedAlpha)
        assertEquals(0.8f, alpha.selectedAlpha)
        assertEquals(0.8f, alpha.focusedSelectedAlpha)
        assertEquals(0.8f, alpha.pressedSelectedAlpha)
        assertEquals(0.8f, alpha.hoveredSelectedAlpha)
        assertEquals(0.6f, alpha.disabledAlpha)
        assertEquals(0.6f, alpha.focusedDisabledAlpha)
        assertEquals(0.6f, alpha.pressedDisabledAlpha)
        assertEquals(0.6f, alpha.hoveredDisabledAlpha)
    }

    @Test
    fun partiallyCustomizedStyleSharesOmittedDefaults() {
        val customColors = StyleDefaults.colors(backgroundColor = Color.Red)
        val style = StyleDefaults.style(colors = customColors)

        assertNotSame(StyleDefaults.None, style)
        assertSame(customColors, style.colors)
        assertSame(StyleDefaults.None.borders, style.borders)
        assertSame(StyleDefaults.None.scale, style.scale)
        assertSame(StyleDefaults.None.shapes, style.shapes)
        assertSame(StyleDefaults.None.alpha, style.alpha)
    }
}
