// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.ComponentStyleScope
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.interactionStyle
import io.daio.wild.style.styleSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleSpecIntegrationTest {
    private val base =
        StyleDefaults.style(
            colors =
                StyleDefaults.colors(
                    backgroundColor = Color.Black,
                    focusedBackgroundColor = Color.Blue,
                    contentColor = Color.White,
                    focusedContentColor = Color.Yellow,
                ),
            scale = StyleDefaults.scale(scale = 1f, focusedScale = 1.2f),
            alpha = StyleDefaults.alpha(alpha = 1f, focusedAlpha = 0.8f),
        )

    @Test
    fun baseThenTwoBlocks_laterWins() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val spec =
                styleSpec(base) {
                    color = Color.Red
                    scale = 1.5f
                }.then {
                    color = Color.Green
                }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = spec)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()

            assertEquals(Color.Green, recorder.last.color)
            assertEquals(1.5f, recorder.last.scale)
            assertEquals(base.alpha.alpha, recorder.last.alpha)
        }

    @Test
    fun earlierWritesRemainSameEval() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val spec =
                styleSpec(base) {
                    scale = 1.5f
                    alpha = 0.5f
                }.then {
                    color = Color.Magenta
                }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = spec)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()

            assertEquals(Color.Magenta, recorder.last.color)
            assertEquals(1.5f, recorder.last.scale)
            assertEquals(0.5f, recorder.last.alpha)
        }

    @Test
    fun omittedAssignment_recoversBaseNextEval() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            var overrideColor by mutableStateOf(true)
            val override: ComponentStyleScope.() -> Unit = {
                if (overrideColor) {
                    color = Color.Red
                }
            }
            val spec = styleSpec(base, override)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = spec)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { overrideColor = false }
            waitForIdle()

            assertEquals(base.colors.backgroundColor, recorder.last.color)
        }

    @Test
    fun sharedSpec_independentSurfaceState() =
        runComposeUiTest {
            val first = StyleRecorder()
            val second = StyleRecorder()
            val spec =
                styleSpec(base) {
                    // No chrome overrides — surfaces resolve independently from shared base tables.
                }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, enabled = true, selected = false, style = spec)
                        .recordStyle(first),
                )
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, enabled = false, selected = true, style = spec)
                        .recordStyle(second),
                )
            }
            waitForIdle()

            assertEquals(
                base.colors.colorFor(
                    enabled = true,
                    focused = false,
                    hovered = false,
                    pressed = false,
                    selected = false,
                ),
                first.last.color,
            )
            assertEquals(
                base.colors.colorFor(
                    enabled = false,
                    focused = false,
                    hovered = false,
                    pressed = false,
                    selected = true,
                ),
                second.last.color,
            )
            assertEquals(true, first.last.enabled)
            assertEquals(false, second.last.enabled)
            assertEquals(false, first.last.selected)
            assertEquals(true, second.last.selected)
        }

    @Test
    fun flagMatrix32_includingContentBase() {
        for (enabledFlag in listOf(true, false)) {
            for (selectedFlag in listOf(false, true)) {
                for (focused in listOf(false, true)) {
                    for (hovered in listOf(false, true)) {
                        for (pressed in listOf(false, true)) {
                            assertFlagCombo(
                                enabledFlag = enabledFlag,
                                selectedFlag = selectedFlag,
                                focused = focused,
                                hovered = hovered,
                                pressed = pressed,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun assertFlagCombo(
        enabledFlag: Boolean,
        selectedFlag: Boolean,
        focused: Boolean,
        hovered: Boolean,
        pressed: Boolean,
    ) = runComposeUiTest {
        val contentCaptures = mutableListOf<Color>()
        val capture: ComponentStyleScope.() -> Unit = {
            contentCaptures += contentColor
        }
        val spec = styleSpec(base, capture)
        val recorder = StyleRecorder()
        val source = MutableInteractionSource()

        setContent {
            Box(
                Modifier
                    .size(1.dp)
                    .interactionStyle(
                        interactionSource = source,
                        enabled = enabledFlag,
                        selected = selectedFlag,
                        style = spec,
                    )
                    .recordStyle(recorder),
            )
        }
        waitForIdle()

        if (focused || hovered || pressed) {
            runOnIdle {
                if (focused) assertTrue(source.tryEmit(FocusInteraction.Focus()))
                if (hovered) assertTrue(source.tryEmit(HoverInteraction.Enter()))
                if (pressed) assertTrue(source.tryEmit(PressInteraction.Press(Offset.Zero)))
            }
            waitForIdle()
        }

        val label = "flags=$enabledFlag/$selectedFlag/$focused/$hovered/$pressed"
        assertEquals(
            base.colors.colorFor(
                enabled = enabledFlag,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selectedFlag,
            ),
            recorder.last.color,
            "color $label",
        )
        assertEquals(
            base.scale.scaleFor(
                enabled = enabledFlag,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selectedFlag,
            ),
            recorder.last.scale,
            "scale $label",
        )
        assertEquals(
            base.alpha.alphaFor(
                enabled = enabledFlag,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selectedFlag,
            ),
            recorder.last.alpha,
            "alpha $label",
        )
        assertEquals(
            base.colors.contentColorFor(
                enabled = enabledFlag,
                focused = focused,
                hovered = hovered,
                pressed = pressed,
                selected = selectedFlag,
            ),
            contentCaptures.last(),
            "contentColor $label",
        )
    }
}
