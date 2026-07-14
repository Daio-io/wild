// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.interactionStyle
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleValueRecompositionTest {
    @Test
    fun unrelatedRecompositionDoesNotReresolveStableStyle() =
        runComposeUiTest {
            val style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))
            val recorder = StyleRecorder()
            var tick by mutableStateOf(0)

            setContent {
                // read tick so parent recomposes
                tick
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = style)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            StyleParentTestCounters.reset()
            val dispatchesBefore = recorder.snapshots.size

            repeat(5) {
                runOnIdle { tick++ }
                waitForIdle()
            }

            assertEquals(0, StyleParentTestCounters.updateCount)
            assertEquals(0, StyleParentTestCounters.resolveCount)
            assertEquals(dispatchesBefore, recorder.snapshots.size)
        }

    @Test
    fun equalStyleReplacementDoesNotReresolve() =
        runComposeUiTest {
            val styleA = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))
            var style by mutableStateOf(styleA)
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = style)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            StyleParentTestCounters.reset()
            val dispatchesBefore = recorder.snapshots.size

            runOnIdle {
                style =
                    styleA.copy(
                        colors = styleA.colors.copy(backgroundColor = Color.Red),
                    )
            }
            waitForIdle()

            assertEquals(0, StyleParentTestCounters.updateCount)
            assertEquals(0, StyleParentTestCounters.resolveCount)
            assertEquals(dispatchesBefore, recorder.snapshots.size)
        }

    @Test
    fun stylePropertyChangeResolvesOnce() =
        runComposeUiTest {
            var style by mutableStateOf(
                StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red)),
            )
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, style = style)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            StyleParentTestCounters.reset()

            runOnIdle {
                style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Green))
            }
            waitForIdle()

            assertEquals(1, StyleParentTestCounters.updateCount)
            assertEquals(1, StyleParentTestCounters.resolveCount)
            assertEquals(Color.Green, recorder.last.color)
        }

    @Test
    fun enabledOrSelectedChangeResolvesOnce() =
        runComposeUiTest {
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = Color.Red,
                            disabledBackgroundColor = Color.Gray,
                            selectedBackgroundColor = Color.Blue,
                        ),
                )
            var enabled by mutableStateOf(true)
            var selected by mutableStateOf(false)
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(
                            interactionSource = null,
                            enabled = enabled,
                            selected = selected,
                            style = style,
                        )
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            StyleParentTestCounters.reset()

            runOnIdle { enabled = false }
            waitForIdle()
            assertEquals(1, StyleParentTestCounters.updateCount)
            assertEquals(1, StyleParentTestCounters.resolveCount)
            assertEquals(Color.Gray, recorder.last.color)

            StyleParentTestCounters.reset()
            runOnIdle {
                enabled = true
                selected = true
            }
            waitForIdle()
            assertEquals(1, StyleParentTestCounters.updateCount)
            assertEquals(1, StyleParentTestCounters.resolveCount)
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun interactionChangeStillResolves() =
        runComposeUiTest {
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = Color.Red,
                            focusedBackgroundColor = Color.Blue,
                        ),
                )
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = source, style = style)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            StyleParentTestCounters.reset()

            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()

            assertTrue(StyleParentTestCounters.resolveCount >= 1)
            assertEquals(Color.Blue, recorder.last.color)
        }
}
