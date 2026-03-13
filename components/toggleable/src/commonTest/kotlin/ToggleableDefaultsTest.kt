// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
