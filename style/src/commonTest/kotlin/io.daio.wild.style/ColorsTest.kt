// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorsTest {
    @Test
    fun defaultStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.backgroundColor, bgColor)
        assertEquals(colors.contentColor, contentColor)
    }

    @Test
    fun focusedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.focusedBackgroundColor, bgColor)
        assertEquals(colors.focusedContentColor, contentColor)
    }

    @Test
    fun hoveredStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.hoveredBackgroundColor, bgColor)
        assertEquals(colors.hoveredContentColor, contentColor)
    }

    @Test
    fun pressedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(colors.pressedBackgroundColor, bgColor)
        assertEquals(colors.pressedContentColor, contentColor)
    }

    @Test
    fun selectedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(colors.selectedBackgroundColor, bgColor)
        assertEquals(colors.selectedContentColor, contentColor)
    }

    @Test
    fun disabledStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.disabledBackgroundColor, bgColor)
        assertEquals(colors.disabledContentColor, contentColor)
    }

    @Test
    fun focusedSelectedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(colors.focusedSelectedBackgroundColor, bgColor)
        assertEquals(colors.focusedSelectedContentColor, contentColor)
    }

    @Test
    fun pressedSelectedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )

        assertEquals(colors.pressedSelectedBackgroundColor, bgColor)
        assertEquals(colors.pressedSelectedContentColor, contentColor)
    }

    @Test
    fun hoveredSelectedStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )

        assertEquals(colors.hoveredSelectedBackgroundColor, bgColor)
        assertEquals(colors.hoveredSelectedContentColor, contentColor)
    }

    @Test
    fun focusedDisabledStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.focusedDisabledBackgroundColor, bgColor)
        assertEquals(colors.focusedDisabledContentColor, contentColor)
    }

    @Test
    fun pressedDisabledStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(colors.pressedDisabledBackgroundColor, bgColor)
        assertEquals(colors.pressedDisabledContentColor, contentColor)
    }

    @Test
    fun hoveredDisabledStateColors() {
        val colors = createColors()

        val bgColor =
            colors.colorFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )
        val contentColor =
            colors.contentColorFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(colors.hoveredDisabledBackgroundColor, bgColor)
        assertEquals(colors.hoveredDisabledContentColor, contentColor)
    }

    private fun createColors(): Colors =
        Colors(
            backgroundColor = Color(0xFFFF0000),
            focusedBackgroundColor = Color(0xFF00FF00),
            pressedBackgroundColor = Color(0xFF0000FF),
            hoveredBackgroundColor = Color(0xFFFFFF00),
            selectedBackgroundColor = Color(0xFF00FFFF),
            disabledBackgroundColor = Color(0xFFFF00FF),
            focusedSelectedBackgroundColor = Color(0xFF808080),
            pressedSelectedBackgroundColor = Color(0xFF800000),
            hoveredSelectedBackgroundColor = Color(0xFF008000),
            focusedDisabledBackgroundColor = Color(0xFF000080),
            pressedDisabledBackgroundColor = Color(0xFF808000),
            hoveredDisabledBackgroundColor = Color(0xFF008080),
            contentColor = Color(0xFF000000),
            focusedContentColor = Color(0xFFFFFFFF),
            pressedContentColor = Color(0xFF888888),
            hoveredContentColor = Color(0xFF444444),
            selectedContentColor = Color(0xFFFFA500),
            focusedSelectedContentColor = Color(0xFF800080),
            pressedSelectedContentColor = Color(0xFF8B4513),
            hoveredSelectedContentColor = Color(0xFFFFC0CB),
            disabledContentColor = Color(0xFFA9A9A9),
            focusedDisabledContentColor = Color(0xFF696969),
            pressedDisabledContentColor = Color(0xFFB22222),
            hoveredDisabledContentColor = Color(0xFFD3D3D3),
        )
}
