// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class InteractionsTest {
    @Test
    fun applyInteractionIgnoresDuplicatePress() {
        val press = PressInteraction.Press(Offset.Zero)
        val pressed = Interactions.None.applyInteraction(press)
        assertTrue(pressed.pressed)
        assertSame(pressed, pressed.applyInteraction(PressInteraction.Press(Offset.Zero)))
    }

    @Test
    fun applyInteractionIgnoresReleaseWhenNotPressed() {
        val press = PressInteraction.Press(Offset.Zero)
        val release = PressInteraction.Release(press)
        assertSame(Interactions.None, Interactions.None.applyInteraction(release))
    }

    @Test
    fun applyInteractionIgnoresDuplicateHoverAndFocus() {
        val hoverEnter = HoverInteraction.Enter()
        val hovered = Interactions.None.applyInteraction(hoverEnter)
        assertSame(hovered, hovered.applyInteraction(HoverInteraction.Enter()))

        val focus = FocusInteraction.Focus()
        val focused = Interactions.None.applyInteraction(focus)
        assertSame(focused, focused.applyInteraction(focus))
        assertSame(Interactions.None, Interactions.None.applyInteraction(FocusInteraction.Unfocus(focus)))
    }
}
