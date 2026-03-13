// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.ui.state.ToggleableState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ToggleableDefaultsTest {
    @Test
    fun defaultStyleIsNotNull() {
        val style = ToggleableDefaults.style()
        assertNotNull(style)
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
