// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class BorderTest {
    @Test
    fun defaultBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.border, result)
    }

    @Test
    fun focusedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.focusedBorder, result)
    }

    @Test
    fun hoveredBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.hoveredBorder, result)
    }

    @Test
    fun pressedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(borders.pressedBorder, result)
    }

    @Test
    fun selectedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(borders.selectedBorder, result)
    }

    @Test
    fun disabledBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.disabledBorder, result)
    }

    @Test
    fun focusedSelectedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(borders.focusedSelectedBorder, result)
    }

    @Test
    fun pressedSelectedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )

        assertEquals(borders.pressedSelectedBorder, result)
    }

    @Test
    fun hoveredSelectedBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )

        assertEquals(borders.hoveredSelectedBorder, result)
    }

    @Test
    fun focusedDisabledBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.focusedDisabledBorder, result)
    }

    @Test
    fun pressedDisabledBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(borders.pressedDisabledBorder, result)
    }

    @Test
    fun hoveredDisabledBorderState() {
        val borders = createBorders()

        val result =
            borders.borderFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(borders.hoveredDisabledBorder, result)
    }

    private fun createBorders(): Borders =
        Borders(
            border = Border(width = 1.dp, color = Color.Red, inset = 0.dp),
            focusedBorder = Border(width = 2.dp, color = Color.Green, inset = 1.dp),
            hoveredBorder = Border(width = 3.dp, color = Color.Blue, inset = 2.dp),
            pressedBorder = Border(width = 4.dp, color = Color.Yellow, inset = 3.dp),
            selectedBorder = Border(width = 5.dp, color = Color.Cyan, inset = 4.dp),
            disabledBorder = Border(width = 6.dp, color = Color.Magenta, inset = 5.dp),
            focusedSelectedBorder = Border(width = 7.dp, color = Color.Gray, inset = 6.dp),
            pressedSelectedBorder = Border(width = 8.dp, color = Color.Black, inset = 7.dp),
            hoveredSelectedBorder = Border(width = 9.dp, color = Color.White, inset = 8.dp),
            focusedDisabledBorder = Border(width = 10.dp, color = Color(0xFFAAAAAA), inset = 9.dp),
            pressedDisabledBorder = Border(width = 11.dp, color = Color(0xFFBBBBBB), inset = 10.dp),
            hoveredDisabledBorder = Border(width = 12.dp, color = Color(0xFFCCCCCC), inset = 11.dp),
        )
}
