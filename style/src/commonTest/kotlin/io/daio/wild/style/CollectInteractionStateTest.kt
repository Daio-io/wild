// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CollectInteractionStateTest {
    @Test
    fun collectInteractionState_tracksFocus_fromFocusable() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val focusRequester = FocusRequester()
            var capturedFocused = false

            setContent {
                val state by source.collectInteractionStateAsState()
                capturedFocused = state.focused
                Box(
                    Modifier
                        .focusRequester(focusRequester)
                        .focusable(interactionSource = source)
                        .size(1.dp),
                )
            }

            waitForIdle()
            assertFalse(capturedFocused)

            runOnIdle { focusRequester.requestFocus() }
            waitForIdle()
            assertTrue(capturedFocused)
        }

    @Test
    fun collectInteractionState_tracksPressAndHover() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            lateinit var scope: CoroutineScope
            var capturedPressed = false
            var capturedHovered = false

            setContent {
                val state by source.collectInteractionStateAsState()
                scope = rememberCoroutineScope()
                capturedPressed = state.pressed
                capturedHovered = state.hovered
            }

            waitForIdle()
            assertFalse(capturedPressed)
            assertFalse(capturedHovered)

            lateinit var press: PressInteraction.Press
            lateinit var hoverEnter: HoverInteraction.Enter

            runOnIdle {
                scope.launch {
                    press = PressInteraction.Press(Offset.Zero)
                    hoverEnter = HoverInteraction.Enter()
                    source.emit(press)
                    source.emit(hoverEnter)
                }
            }
            waitForIdle()
            assertTrue(capturedPressed)
            assertTrue(capturedHovered)

            runOnIdle {
                scope.launch {
                    source.emit(PressInteraction.Release(press))
                    source.emit(HoverInteraction.Exit(hoverEnter))
                }
            }
            waitForIdle()
            assertFalse(capturedPressed)
            assertFalse(capturedHovered)
        }
}
