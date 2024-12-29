// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleTest {
    @Test
    fun defaultStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.scale, result)
    }

    @Test
    fun focusedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.focusedScale, result)
    }

    @Test
    fun hoveredStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.hoveredScale, result)
    }

    @Test
    fun pressedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(scale.pressedScale, result)
    }

    @Test
    fun selectedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(scale.selectedScale, result)
    }

    @Test
    fun disabledStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.disabledScale, result)
    }

    @Test
    fun focusedSelectedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = true,
                hovered = false,
                pressed = false,
                selected = true,
            )

        assertEquals(scale.focusedSelectedScale, result)
    }

    @Test
    fun pressedSelectedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = false,
                pressed = true,
                selected = true,
            )

        assertEquals(scale.pressedSelectedScale, result)
    }

    @Test
    fun hoveredSelectedStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = true,
                focused = false,
                hovered = true,
                pressed = false,
                selected = true,
            )

        assertEquals(scale.hoveredSelectedScale, result)
    }

    @Test
    fun focusedDisabledStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = false,
                focused = true,
                hovered = false,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.focusedDisabledScale, result)
    }

    @Test
    fun pressedDisabledStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = false,
                focused = false,
                hovered = false,
                pressed = true,
                selected = false,
            )

        assertEquals(scale.pressedDisabledScale, result)
    }

    @Test
    fun hoveredDisabledStateScale() {
        val scale = createScales()

        val result =
            scale.scaleFor(
                enabled = false,
                focused = false,
                hovered = true,
                pressed = false,
                selected = false,
            )

        assertEquals(scale.hoveredDisabledScale, result)
    }

    private fun createScales(): Scale =
        Scale(
            scale = 1f,
            focusedScale = 1.1f,
            hoveredScale = 1.2f,
            pressedScale = 1.3f,
            selectedScale = 1.4f,
            disabledScale = 1.5f,
            focusedSelectedScale = 1.6f,
            pressedSelectedScale = 1.7f,
            hoveredSelectedScale = 1.8f,
            focusedDisabledScale = 1.9f,
            pressedDisabledScale = 2.0f,
            hoveredDisabledScale = 2.1f,
        )
}
