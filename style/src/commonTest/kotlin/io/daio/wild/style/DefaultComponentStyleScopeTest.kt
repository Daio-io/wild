// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Equality contract for [DefaultComponentStyleScope], mirroring [DefaultStyleScope] plus
 * [ComponentStyleScope.contentColor].
 */
@OptIn(ExperimentalWildApi::class)
class DefaultComponentStyleScopeTest {
    @Test
    fun equalityIsBasedOnAllFields() {
        val scope1 = DefaultComponentStyleScope()
        val scope2 = DefaultComponentStyleScope()
        assertEquals(scope1, scope2)
        assertEquals(scope1.hashCode(), scope2.hashCode())
    }

    @Test
    fun unequalAfterStateChange() {
        val scope1 = DefaultComponentStyleScope()
        val scope2 = DefaultComponentStyleScope()

        scope1.updateState(
            enabled = true,
            focused = true,
            selected = false,
            pressed = false,
            hovered = false,
        )

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun unequalWhenContentColorDiffers() {
        val scope1 = DefaultComponentStyleScope()
        val scope2 = DefaultComponentStyleScope()

        scope1.contentColor = Color.Red

        assertNotEquals(scope1, scope2)
        assertNotEquals(scope1.hashCode(), scope2.hashCode())
    }

    @Test
    fun equalWhenChromeFlagsAndContentColorMatch() {
        val scope1 = DefaultComponentStyleScope()
        val scope2 = DefaultComponentStyleScope()

        scope1.color = Color.Blue
        scope1.alpha = 0.5f
        scope1.scale = 1.2f
        scope1.contentColor = Color.Green
        scope1.updateState(
            enabled = false,
            focused = true,
            selected = true,
            pressed = false,
            hovered = true,
        )

        scope2.color = Color.Blue
        scope2.alpha = 0.5f
        scope2.scale = 1.2f
        scope2.contentColor = Color.Green
        scope2.updateState(
            enabled = false,
            focused = true,
            selected = true,
            pressed = false,
            hovered = true,
        )

        assertEquals(scope1, scope2)
        assertEquals(scope1.hashCode(), scope2.hashCode())
    }
}
