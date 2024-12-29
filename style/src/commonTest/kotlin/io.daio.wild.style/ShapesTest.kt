// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapesTest {
    @Test
    fun defaultStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.shape, result)
    }

    @Test
    fun focusedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.focusedShape, result)
    }

    @Test
    fun hoveredStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.hoveredShape, result)
    }

    @Test
    fun pressedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(shapes.pressedShape, result)
    }

    @Test
    fun selectedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(shapes.selectedShape, result)
    }

    @Test
    fun disabledStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.disabledShape, result)
    }

    @Test
    fun focusedSelectedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(shapes.focusedSelectedShape, result)
    }

    @Test
    fun pressedSelectedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )

        assertEquals(shapes.pressedSelectedShape, result)
    }

    @Test
    fun hoveredSelectedStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )

        assertEquals(shapes.hoveredSelectedShape, result)
    }

    @Test
    fun focusedDisabledStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.focusedDisabledShape, result)
    }

    @Test
    fun pressedDisabledStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(shapes.pressedDisabledShape, result)
    }

    @Test
    fun hoveredDisabledStateShape() {
        val shapes = createShapes()

        val result =
            shapes.shapeFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(shapes.hoveredDisabledShape, result)
    }

    private fun createShapes(): Shapes =
        Shapes(
            shape = RectangleShape,
            focusedShape = RoundedCornerShape(4.dp),
            hoveredShape = RoundedCornerShape(8.dp),
            pressedShape = RoundedCornerShape(12.dp),
            selectedShape = RoundedCornerShape(16.dp),
            disabledShape = RoundedCornerShape(20.dp),
            focusedSelectedShape = RoundedCornerShape(24.dp),
            pressedSelectedShape = RoundedCornerShape(28.dp),
            hoveredSelectedShape = RoundedCornerShape(32.dp),
            focusedDisabledShape = RoundedCornerShape(36.dp),
            pressedDisabledShape = RoundedCornerShape(40.dp),
            hoveredDisabledShape = RoundedCornerShape(44.dp),
        )
}
