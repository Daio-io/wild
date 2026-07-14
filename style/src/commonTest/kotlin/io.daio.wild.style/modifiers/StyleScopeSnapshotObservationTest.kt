// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Border
import io.daio.wild.style.StyleScope
import io.daio.wild.style.interactionStyle
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleScopeSnapshotObservationTest {
    @Test
    fun snapshotColorChangeUpdatesWithoutInteraction() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val colorState = mutableStateOf(Color.Red)
            val stableBlock: StyleScope.() -> Unit = {
                color = colorState.value
            }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, block = stableBlock)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { colorState.value = Color.Blue }
            waitForIdle()

            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun snapshotDrivenAlphaShapeBorderScaleAndAnimationSpecUpdate() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val initialBorder = Border(width = 1.dp, color = Color.Red)
            val updatedBorder = Border(width = 2.dp, color = Color.Green)
            val initialSpec = spring<Float>()
            val updatedSpec = spring<Float>(stiffness = 500f)
            val styleState =
                mutableStateOf(
                    SnapshotStyleBundle(
                        alpha = 1f,
                        shape = RectangleShape,
                        border = initialBorder,
                        scale = 1f,
                        scaleAnimationSpec = initialSpec,
                    ),
                )
            val stableBlock: StyleScope.() -> Unit = {
                val bundle = styleState.value
                alpha = bundle.alpha
                shape = bundle.shape
                border = bundle.border
                scale = bundle.scale
                scaleAnimationSpec = bundle.scaleAnimationSpec
                color = Color.Black
            }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, block = stableBlock)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(1f, recorder.last.alpha)
            assertSame(RectangleShape, recorder.last.shape)
            assertEquals(initialBorder, recorder.last.border)
            assertEquals(1f, recorder.last.scale)
            assertSame(initialSpec, recorder.last.scaleAnimationSpec)

            runOnIdle {
                styleState.value =
                    SnapshotStyleBundle(
                        alpha = 0.5f,
                        shape = CircleShape,
                        border = updatedBorder,
                        scale = 1.2f,
                        scaleAnimationSpec = updatedSpec,
                    )
            }
            waitForIdle()

            assertEquals(0.5f, recorder.last.alpha)
            assertSame(CircleShape, recorder.last.shape)
            assertEquals(updatedBorder, recorder.last.border)
            assertEquals(1.2f, recorder.last.scale)
            assertSame(updatedSpec, recorder.last.scaleAnimationSpec)
        }

    @Test
    fun snapshotEqualEffectiveStyleDoesNotRedispatch() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val styleKeyState = mutableStateOf(0)
            val stableBlock: StyleScope.() -> Unit = {
                color =
                    when (styleKeyState.value) {
                        0,
                        1,
                        -> Color.Red
                        2,
                        3,
                        -> Color.Blue
                        else -> Color.Black
                    }
            }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, block = stableBlock)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            val initialUpdates = recorder.snapshots.size
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { styleKeyState.value = 1 }
            waitForIdle()
            assertEquals(initialUpdates, recorder.snapshots.size)
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { styleKeyState.value = 2 }
            waitForIdle()
            assertEquals(initialUpdates + 1, recorder.snapshots.size)
            assertEquals(Color.Blue, recorder.last.color)
            val afterBlueDispatch = recorder.snapshots.size

            runOnIdle { styleKeyState.value = 3 }
            waitForIdle()
            assertEquals(afterBlueDispatch, recorder.snapshots.size)
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun replacingBlockStopsObservingOldState() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val stateA = mutableStateOf(Color.Red)
            val stateB = mutableStateOf(Color.Green)
            val blockA: StyleScope.() -> Unit = { color = stateA.value }
            val blockB: StyleScope.() -> Unit = { color = stateB.value }
            var useBlockA by mutableStateOf(true)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(
                            interactionSource = null,
                            block = if (useBlockA) blockA else blockB,
                        )
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { useBlockA = false }
            waitForIdle()
            assertEquals(Color.Green, recorder.last.color)
            val afterSwitch = recorder.snapshots.size

            runOnIdle { stateA.value = Color.Blue }
            waitForIdle()
            assertEquals(afterSwitch, recorder.snapshots.size)
            assertEquals(Color.Green, recorder.last.color)

            runOnIdle { stateB.value = Color.Yellow }
            waitForIdle()
            assertEquals(Color.Yellow, recorder.last.color)
            assertEquals(afterSwitch + 1, recorder.snapshots.size)
        }

    @Test
    fun detachedNodeIgnoresSnapshotUntilReattach() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val colorState = mutableStateOf(Color.Red)
            val stableBlock: StyleScope.() -> Unit = { color = colorState.value }
            var showChild by mutableStateOf(true)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, block = stableBlock),
                ) {
                    if (showChild) {
                        Box(Modifier.size(1.dp).recordStyle(recorder))
                    }
                }
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { showChild = false }
            waitForIdle()
            assertEquals(0, recorder.snapshots.size)

            runOnIdle { colorState.value = Color.Blue }
            waitForIdle()
            assertEquals(0, recorder.snapshots.size)

            runOnIdle { showChild = true }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun nestedParentsObserveIndependentSnapshotState() =
        runComposeUiTest {
            val outerRecorder = StyleRecorder()
            val innerRecorder = StyleRecorder()
            val outerColorState = mutableStateOf(Color.Red)
            val innerColorState = mutableStateOf(Color.Green)
            val outerBlock: StyleScope.() -> Unit = { color = outerColorState.value }
            val innerBlock: StyleScope.() -> Unit = { color = innerColorState.value }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null, block = outerBlock),
                ) {
                    Box(Modifier.size(1.dp).recordStyle(outerRecorder))
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(interactionSource = null, block = innerBlock)
                            .recordStyle(innerRecorder),
                    )
                }
            }
            waitForIdle()
            assertEquals(Color.Red, outerRecorder.last.color)
            assertEquals(Color.Green, innerRecorder.last.color)
            val innerUpdates = innerRecorder.snapshots.size

            runOnIdle { outerColorState.value = Color.Blue }
            waitForIdle()
            assertEquals(Color.Blue, outerRecorder.last.color)
            assertEquals(innerUpdates, innerRecorder.snapshots.size)
            assertEquals(Color.Green, innerRecorder.last.color)

            runOnIdle { innerColorState.value = Color.Yellow }
            waitForIdle()
            assertEquals(Color.Yellow, innerRecorder.last.color)
            assertEquals(Color.Blue, outerRecorder.last.color)
        }

    @Test
    fun interactionAndSnapshotChangesSettleWithoutReentrantDuplicates() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val source = MutableInteractionSource()
            val colorState = mutableStateOf(Color.Red)
            val stableBlock: StyleScope.() -> Unit = {
                color = colorState.value
                if (focused) scale = 1.2f
            }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = source, block = stableBlock)
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)
            assertEquals(1f, recorder.last.scale)
            val initialUpdates = recorder.snapshots.size

            runOnIdle { colorState.value = Color.Blue }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)
            assertEquals(1f, recorder.last.scale)
            val afterSnapshotOnly = recorder.snapshots.size
            assertEquals(initialUpdates + 1, afterSnapshotOnly)

            runOnIdle {
                colorState.value = Color.Green
                runBlocking { source.emit(FocusInteraction.Focus()) }
            }
            waitForIdle()

            assertTrue(recorder.last.focused)
            assertEquals(Color.Green, recorder.last.color)
            assertEquals(1.2f, recorder.last.scale)
            assertTrue(
                recorder.snapshots.size <= afterSnapshotOnly + 2,
                "Expected at most 2 snapshot updates but got ${recorder.snapshots.size - afterSnapshotOnly}",
            )
        }
}
