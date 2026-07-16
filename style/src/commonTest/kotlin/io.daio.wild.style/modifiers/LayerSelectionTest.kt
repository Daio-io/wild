// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LayerSelectionTest {
    @Test
    fun scaleLayerIsSkippedForDefaultValues() {
        assertFalse(needsScaleLayer(animatedScale = 1f, zIndex = 0f))
    }

    @Test
    fun scaleLayerIsUsedForScaleOrZIndex() {
        assertTrue(needsScaleLayer(animatedScale = 1.01f, zIndex = 0f))
        assertTrue(needsScaleLayer(animatedScale = 1f, zIndex = 0.5f))
    }

    @Test
    fun shapeLayerIsSkippedForOpaqueRectangle() {
        assertFalse(needsShapeLayer(shape = RectangleShape, alpha = 1f))
    }

    @Test
    fun shapeLayerIsUsedForNonRectangleOrTransparency() {
        assertTrue(needsShapeLayer(shape = testShape, alpha = 1f))
        assertTrue(needsShapeLayer(shape = RectangleShape, alpha = 0.5f))
    }

    private val testShape =
        object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density,
            ): Outline = Outline.Rectangle(Rect(0f, 0f, size.width, size.height))
        }
}
