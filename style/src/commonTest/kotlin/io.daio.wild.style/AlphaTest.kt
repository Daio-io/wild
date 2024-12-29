// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphaTest {
    @Test
    fun defaultAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.alpha, result)
    }

    @Test
    fun focusedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.focusedAlpha, result)
    }

    @Test
    fun hoveredAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.hoveredAlpha, result)
    }

    @Test
    fun pressedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(alpha.pressedAlpha, result)
    }

    @Test
    fun selectedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(alpha.selectedAlpha, result)
    }

    @Test
    fun disabledAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.disabledAlpha, result)
    }

    @Test
    fun focusedSelectedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(alpha.focusedSelectedAlpha, result)
    }

    @Test
    fun pressedSelectedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )

        assertEquals(alpha.pressedSelectedAlpha, result)
    }

    @Test
    fun hoveredSelectedAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )

        assertEquals(alpha.hoveredSelectedAlpha, result)
    }

    @Test
    fun focusedDisabledAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.focusedDisabledAlpha, result)
    }

    @Test
    fun pressedDisabledAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(alpha.pressedDisabledAlpha, result)
    }

    @Test
    fun hoveredDisabledAlphaState() {
        val alpha = createAlpha()

        val result =
            alpha.alphaFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(alpha.hoveredDisabledAlpha, result)
    }

    private fun createAlpha(): Alpha =
        Alpha(
            alpha = 0.1f,
            focusedAlpha = 0.2f,
            hoveredAlpha = 0.3f,
            pressedAlpha = 0.4f,
            selectedAlpha = 0.5f,
            disabledAlpha = 0.6f,
            focusedSelectedAlpha = 0.7f,
            pressedSelectedAlpha = 0.8f,
            hoveredSelectedAlpha = 0.9f,
            focusedDisabledAlpha = 1.0f,
            pressedDisabledAlpha = 0.11f,
            hoveredDisabledAlpha = 0.12f,
        )
}
