// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Tests for the DefaultStyleScope state management that drives traversal updates.
 *
 * Note: Full traversal integration tests (verifying parent→child node communication
 * via traverseDirectDescendants) require a running Compose runtime with attached nodes.
 * These tests verify the state management logic and style resolution that the traversal
 * system depends on.
 */
class DefaultStyleScopeTest {
    @Test
    fun defaultValuesAreCorrect() {
        val scope = DefaultStyleScope()
        assertEquals(false, scope.focused)
        assertEquals(false, scope.hovered)
        assertEquals(false, scope.pressed)
        assertEquals(false, scope.selected)
        assertEquals(true, scope.enabled)
    }

    @Test
    fun updateStateReflectsInScope() {
        val scope = DefaultStyleScope()
        scope.updateState(
            enabled = false,
            focused = true,
            selected = true,
            pressed = true,
            hovered = true,
        )

        assertEquals(true, scope.focused)
        assertEquals(true, scope.hovered)
        assertEquals(true, scope.pressed)
        assertEquals(true, scope.selected)
        assertEquals(false, scope.enabled)
    }

    @Test
    fun updateStatePartialChange() {
        val scope = DefaultStyleScope()
        scope.updateState(
            enabled = true,
            focused = true,
            selected = false,
            pressed = false,
            hovered = false,
        )

        assertEquals(true, scope.focused)
        assertEquals(false, scope.hovered)
        assertEquals(false, scope.pressed)
        assertEquals(false, scope.selected)
        assertEquals(true, scope.enabled)
    }

    @Test
    fun equalityIsBasedOnAllFields() {
        val scope1 = DefaultStyleScope()
        val scope2 = DefaultStyleScope()
        assertEquals(scope1, scope2)
        assertEquals(scope1.hashCode(), scope2.hashCode())
    }

    @Test
    fun unequalAfterStateChange() {
        val scope1 = DefaultStyleScope()
        val scope2 = DefaultStyleScope()

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
    fun stylePropertiesPersistAcrossStateUpdates() {
        val scope = DefaultStyleScope()
        scope.color = androidx.compose.ui.graphics.Color.Red
        scope.scale = 1.5f
        scope.alpha = 0.8f

        scope.updateState(
            enabled = true,
            focused = true,
            selected = false,
            pressed = false,
            hovered = false,
        )

        // Style properties should survive state updates (they're set by the block, not reset)
        assertEquals(androidx.compose.ui.graphics.Color.Red, scope.color)
        assertEquals(1.5f, scope.scale)
        assertEquals(0.8f, scope.alpha)
    }
}

/**
 * Tests that verify the style resolution functions correctly map interaction states
 * to style values. These are the functions called inside the StyleScope block during
 * traversal updates.
 */
class StyleResolutionForTraversalTest {
    @Test
    fun colorsResolveCorrectlyForFocusedState() {
        val colors =
            StyleDefaults.colors(
                backgroundColor = androidx.compose.ui.graphics.Color.Black,
                focusedBackgroundColor = androidx.compose.ui.graphics.Color.Blue,
            )

        val scope = DefaultStyleScope()
        scope.updateState(enabled = true, focused = true, selected = false, pressed = false, hovered = false)

        scope.color =
            colors.colorFor(
                enabled = scope.enabled,
                focused = scope.focused,
                hovered = scope.hovered,
                pressed = scope.pressed,
                selected = scope.selected,
            )

        assertEquals(androidx.compose.ui.graphics.Color.Blue, scope.color)
    }

    @Test
    fun colorsResolveCorrectlyForSelectedState() {
        val colors =
            StyleDefaults.colors(
                backgroundColor = androidx.compose.ui.graphics.Color.Black,
                selectedBackgroundColor = androidx.compose.ui.graphics.Color.Green,
            )

        val scope = DefaultStyleScope()
        scope.updateState(enabled = true, focused = false, selected = true, pressed = false, hovered = false)

        scope.color =
            colors.colorFor(
                enabled = scope.enabled,
                focused = scope.focused,
                hovered = scope.hovered,
                pressed = scope.pressed,
                selected = scope.selected,
            )

        assertEquals(androidx.compose.ui.graphics.Color.Green, scope.color)
    }

    @Test
    fun colorsResolveCorrectlyForFocusedAndSelectedState() {
        val colors =
            StyleDefaults.colors(
                backgroundColor = androidx.compose.ui.graphics.Color.Black,
                focusedBackgroundColor = androidx.compose.ui.graphics.Color.Blue,
                selectedBackgroundColor = androidx.compose.ui.graphics.Color.Green,
                focusedSelectedBackgroundColor = androidx.compose.ui.graphics.Color.Cyan,
            )

        val scope = DefaultStyleScope()
        scope.updateState(enabled = true, focused = true, selected = true, pressed = false, hovered = false)

        scope.color =
            colors.colorFor(
                enabled = scope.enabled,
                focused = scope.focused,
                hovered = scope.hovered,
                pressed = scope.pressed,
                selected = scope.selected,
            )

        assertEquals(androidx.compose.ui.graphics.Color.Cyan, scope.color)
    }

    @Test
    fun disabledStateOverridesOtherStates() {
        val colors =
            StyleDefaults.colors(
                backgroundColor = androidx.compose.ui.graphics.Color.Black,
                focusedBackgroundColor = androidx.compose.ui.graphics.Color.Blue,
                disabledBackgroundColor = androidx.compose.ui.graphics.Color.Gray,
            )

        val scope = DefaultStyleScope()
        scope.updateState(enabled = false, focused = true, selected = false, pressed = false, hovered = false)

        scope.color =
            colors.colorFor(
                enabled = scope.enabled,
                focused = scope.focused,
                hovered = scope.hovered,
                pressed = scope.pressed,
                selected = scope.selected,
            )

        // When disabled+focused, should use focusedDisabledBackgroundColor which defaults to disabledBackgroundColor
        assertEquals(androidx.compose.ui.graphics.Color.Gray, scope.color)
    }

    @Test
    fun fullStyleBlockExecutesAllProperties() {
        val style =
            StyleDefaults.style(
                colors =
                    StyleDefaults.colors(
                        backgroundColor = androidx.compose.ui.graphics.Color.Red,
                    ),
                scale = StyleDefaults.scale(focusedScale = 1.2f),
                alpha = StyleDefaults.alpha(disabledAlpha = 0.5f),
            )

        val scope = DefaultStyleScope()
        scope.updateState(enabled = true, focused = true, selected = false, pressed = false, hovered = false)

        // Simulate what the style block does during traversal
        scope.color =
            style.colors.colorFor(
                enabled = scope.enabled, focused = scope.focused,
                hovered = scope.hovered, pressed = scope.pressed, selected = scope.selected,
            )
        scope.scale =
            style.scale.scaleFor(
                enabled = scope.enabled, focused = scope.focused,
                hovered = scope.hovered, pressed = scope.pressed, selected = scope.selected,
            )
        scope.alpha =
            style.alpha.alphaFor(
                enabled = scope.enabled, focused = scope.focused,
                hovered = scope.hovered, pressed = scope.pressed, selected = scope.selected,
            )

        assertEquals(androidx.compose.ui.graphics.Color.Red, scope.color)
        assertEquals(1.2f, scope.scale)
        assertEquals(1f, scope.alpha) // focused alpha defaults to base alpha (1f)
    }
}
