// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import io.daio.wild.style.modifiers.InteractionBits
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class InteractionBitsTest {
    @Test
    fun applyInteraction_shortCircuitsDuplicatePress() {
        val press = PressInteraction.Press(Offset.Zero)
        val pressed = InteractionBits.Empty.applyInteraction(press)
        assertTrue(pressed.pressed)
        assertSame(pressed, pressed.applyInteraction(PressInteraction.Press(Offset.Zero)))
    }

    @Test
    fun applyInteraction_shortCircuitsReleaseWhenNotPressed() {
        val press = PressInteraction.Press(Offset.Zero)
        val release = PressInteraction.Release(press)
        assertSame(InteractionBits.Empty, InteractionBits.Empty.applyInteraction(release))
    }

    @Test
    fun applyInteraction_shortCircuitsDuplicateHoverAndFocus() {
        val hoverEnter = HoverInteraction.Enter()
        val hovered = InteractionBits.Empty.applyInteraction(hoverEnter)
        assertSame(hovered, hovered.applyInteraction(HoverInteraction.Enter()))

        val focus = FocusInteraction.Focus()
        val focused = InteractionBits.Empty.applyInteraction(focus)
        assertSame(focused, focused.applyInteraction(focus))
        assertSame(InteractionBits.Empty, InteractionBits.Empty.applyInteraction(FocusInteraction.Unfocus(focus)))
    }
}
