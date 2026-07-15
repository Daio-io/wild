// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
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

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleValueRecompositionTest {
    @Test
    fun unrelatedRecompositionDoesNotRedispatchStableStyle() =
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
            val dispatchesBefore = recorder.snapshots.size
            val colorBefore = recorder.last.color

            repeat(5) {
                runOnIdle { tick++ }
                waitForIdle()
            }

            assertEquals(dispatchesBefore, recorder.snapshots.size)
            assertEquals(colorBefore, recorder.last.color)
        }

    @Test
    fun equalStyleReplacementDoesNotRedispatch() =
        runComposeUiTest {
            val styleA = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))
            var style by mutableStateOf(styleA, policy = referentialEqualityPolicy())
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
            val dispatchesBefore = recorder.snapshots.size

            runOnIdle {
                style =
                    styleA.copy(
                        colors = styleA.colors.copy(backgroundColor = Color.Red),
                    )
            }
            waitForIdle()

            assertEquals(dispatchesBefore, recorder.snapshots.size)
            assertEquals(Color.Red, recorder.last.color)
        }

    @Test
    fun stylePropertyChangeDispatchesOnce() =
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
            val dispatchesBefore = recorder.snapshots.size

            runOnIdle {
                style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Green))
            }
            waitForIdle()

            assertEquals(dispatchesBefore + 1, recorder.snapshots.size)
            assertEquals(Color.Green, recorder.last.color)
        }

    @Test
    fun enabledOrSelectedChangeDispatchesOnce() =
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
            var dispatchesBefore = recorder.snapshots.size

            runOnIdle { enabled = false }
            waitForIdle()
            assertEquals(dispatchesBefore + 1, recorder.snapshots.size)
            assertEquals(Color.Gray, recorder.last.color)

            dispatchesBefore = recorder.snapshots.size
            runOnIdle {
                enabled = true
                selected = true
            }
            waitForIdle()
            assertEquals(dispatchesBefore + 1, recorder.snapshots.size)
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun interactionChangeStillDispatches() =
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
            val dispatchesBefore = recorder.snapshots.size

            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()

            assertEquals(dispatchesBefore + 1, recorder.snapshots.size)
            assertEquals(Color.Blue, recorder.last.color)
        }
}
