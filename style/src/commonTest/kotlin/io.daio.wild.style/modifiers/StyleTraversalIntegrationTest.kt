// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Border
import io.daio.wild.style.BorderDefaults
import io.daio.wild.style.StyleScope
import io.daio.wild.style.interactionStyle
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class StyleTraversalIntegrationTest {
    @Test
    fun normalParentChildChainDeliversInitialStyleToEveryChild() =
        runComposeUiTest {
            val first = StyleRecorder()
            val second = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null) { color = Color.Red }
                        .recordStyle(first)
                        .recordStyle(second),
                )
            }

            waitForIdle()
            assertTrue(first.snapshots.isNotEmpty())
            assertTrue(second.snapshots.isNotEmpty())
            assertEquals(Color.Red, first.last.color)
            assertEquals(Color.Red, second.last.color)
        }

    @Test
    fun childAttachedAfterParentRequestsCurrentStyle() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            var showChild by mutableStateOf(false)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null) { color = Color.Green },
                ) {
                    if (showChild) {
                        Box(Modifier.size(1.dp).recordStyle(recorder))
                    }
                }
            }
            waitForIdle()

            runOnIdle { showChild = true }
            waitForIdle()

            assertTrue(recorder.snapshots.isNotEmpty())
            assertEquals(Color.Green, recorder.last.color)
        }

    @Test
    fun initiallyDisabledAndSelectedSurfacesResolveOnFirstAttachedFrame() =
        runComposeUiTest {
            val disabledRecorder = StyleRecorder()
            val selectedRecorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null, enabled = false) {
                            color = if (!enabled) Color.Gray else Color.Red
                        }.recordStyle(disabledRecorder),
                )
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null, selected = true) {
                            color = if (selected) Color.Blue else Color.Red
                        }.recordStyle(selectedRecorder),
                )
            }

            waitForIdle()
            assertEquals(Color.Gray, disabledRecorder.last.color)
            assertFalse(disabledRecorder.last.enabled)
            assertEquals(Color.Blue, selectedRecorder.last.color)
            assertTrue(selectedRecorder.last.selected)
        }

    @Test
    fun effectiveStateChangesDispatchOnceAndRepeatedIdenticalStateDoesNotRedispatch() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()
            lateinit var focus: FocusInteraction.Focus
            lateinit var hover: HoverInteraction.Enter
            lateinit var press: PressInteraction.Press

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            color =
                                when {
                                    pressed -> Color.Red
                                    focused -> Color.Blue
                                    hovered -> Color.Green
                                    else -> Color.Black
                                }
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertTrue(recorder.snapshots.isNotEmpty())
            val initialUpdates = recorder.snapshots.size

            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(initialUpdates + 1, recorder.snapshots.size)
            runBlocking { source.emit(object : Interaction {}) }
            waitForIdle()
            assertEquals(initialUpdates + 1, recorder.snapshots.size)

            runBlocking {
                hover = HoverInteraction.Enter()
                source.emit(hover)
            }
            waitForIdle()
            assertEquals(initialUpdates + 2, recorder.snapshots.size)
            runBlocking {
                press = PressInteraction.Press(Offset.Zero)
                source.emit(press)
            }
            waitForIdle()
            assertEquals(initialUpdates + 3, recorder.snapshots.size)
            runBlocking { source.emit(PressInteraction.Release(press)) }
            waitForIdle()
            assertEquals(initialUpdates + 4, recorder.snapshots.size)
            runBlocking {
                source.emit(HoverInteraction.Exit(hover))
                source.emit(FocusInteraction.Unfocus(focus))
            }
            waitForIdle()
            assertEquals(initialUpdates + 6, recorder.snapshots.size)
        }

    @Test
    fun nestedStyleParentsPreventOuterUpdatesReachingInnerChildren() =
        runComposeUiTest {
            val outer = StyleRecorder()
            val inner = StyleRecorder()
            var outerEnabled by mutableStateOf(true)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null, enabled = outerEnabled) {
                            color = if (enabled) Color.Red else Color.Green
                        },
                ) {
                    Box(Modifier.size(1.dp).recordStyle(outer))
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(null) { color = Color.Blue }
                            .recordStyle(inner),
                    )
                }
            }
            waitForIdle()
            assertEquals(Color.Red, outer.last.color)
            assertEquals(Color.Blue, inner.last.color)
            val innerUpdates = inner.snapshots.size

            runOnIdle { outerEnabled = false }
            waitForIdle()

            assertEquals(Color.Green, outer.last.color)
            assertEquals(innerUpdates, inner.snapshots.size)
            assertEquals(Color.Blue, inner.last.color)
        }

    @Test
    fun siblingStyledComponentsRemainIsolated() =
        runComposeUiTest {
            val first = StyleRecorder()
            val second = StyleRecorder()
            var firstEnabled by mutableStateOf(true)

            setContent {
                Box {
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(null, enabled = firstEnabled) {
                                color = if (enabled) Color.Red else Color.Green
                            }
                            .recordStyle(first),
                    )
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(null) { color = Color.Blue }
                            .recordStyle(second),
                    )
                }
            }
            waitForIdle()
            val secondUpdates = second.snapshots.size

            runOnIdle { firstEnabled = false }
            waitForIdle()

            assertEquals(Color.Green, first.last.color)
            assertEquals(secondUpdates, second.snapshots.size)
            assertEquals(Color.Blue, second.last.color)
        }

    @Test
    fun detachedChildrenReceiveNoUpdatesAndReattachResolvesLatestParentState() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            var showChild by mutableStateOf(true)
            var enabled by mutableStateOf(true)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null, enabled = enabled) {
                            color = if (this.enabled) Color.Red else Color.Blue
                        },
                ) {
                    if (showChild) {
                        Box(Modifier.size(1.dp).recordStyle(recorder))
                    }
                }
            }
            waitForIdle()
            runOnIdle { showChild = false }
            waitForIdle()
            val updatesAfterDetach = recorder.snapshots.size

            runOnIdle { enabled = false }
            waitForIdle()
            assertEquals(updatesAfterDetach, recorder.snapshots.size)

            runOnIdle { showChild = true }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun replacingInteractionSourceCancelsOldCollector() =
        runComposeUiTest {
            val sourceA = MutableInteractionSource()
            val sourceB = MutableInteractionSource()
            val recorder = StyleRecorder()
            var source by mutableStateOf(sourceA)
            lateinit var focusA: FocusInteraction.Focus

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) { color = if (focused) Color.Blue else Color.Red }
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            runBlocking {
                focusA = FocusInteraction.Focus()
                sourceA.emit(focusA)
            }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)

            runOnIdle { source = sourceB }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)
            val afterReplace = recorder.snapshots.size

            runBlocking { sourceA.emit(FocusInteraction.Unfocus(focusA)) }
            waitForIdle()
            assertEquals(afterReplace, recorder.snapshots.size)

            runBlocking { sourceB.emit(FocusInteraction.Focus()) }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)
        }

    @Test
    fun replacingTraversalKeyUsesNewKeyAndDoesNotNotifyStaleKey() =
        runComposeUiTest {
            val oldKey = Any()
            val newKey = Any()
            val oldObserver = InteractionRecorder(oldKey)
            val newObserver = InteractionRecorder(newKey)
            val source = MutableInteractionSource()
            var childKey by mutableStateOf(oldKey)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionSourceNode(source, childKey)
                        .recordInteractions(oldObserver)
                        .recordInteractions(newObserver),
                )
            }
            waitForIdle()
            oldObserver.states.clear()
            newObserver.states.clear()

            runOnIdle { childKey = newKey }
            waitForIdle()
            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()

            assertEquals(
                listOf(
                    Interactions(focused = false, hovered = false, pressed = false),
                    Interactions(focused = true, hovered = false, pressed = false),
                ),
                newObserver.states,
            )
            assertEquals(0, oldObserver.states.size)
        }

    @Test
    fun styleSnapshotIncludesScaleBorderShapeAlphaAndAnimationSpecChanges() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            val border = Border(width = 1.dp, color = Color.Red)
            val spec = spring<Float>()
            var focusedColor by mutableStateOf(Color.Blue)
            val source = MutableInteractionSource()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            color = if (focused) focusedColor else Color.Red
                            alpha = if (focused) 0.5f else 1f
                            scale = if (focused) 1.2f else 1f
                            shape = RectangleShape
                            this.border = border
                            scaleAnimationSpec = spec
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)
            assertEquals(1f, recorder.last.alpha)
            assertEquals(1f, recorder.last.scale)
            assertEquals(border, recorder.last.border)
            assertSame(RectangleShape, recorder.last.shape)
            assertSame(spec, recorder.last.scaleAnimationSpec)

            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()
            assertEquals(focusedColor, recorder.last.color)
            assertEquals(0.5f, recorder.last.alpha)
            assertEquals(1.2f, recorder.last.scale)
            assertNotEquals(recorder.snapshots.first(), recorder.last)
        }

    @Test
    fun conditionalFocusedScaleResetsWhenFocusLost() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            if (focused) scale = 1.1f
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(1f, recorder.last.scale)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(1.1f, recorder.last.scale)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(1f, recorder.last.scale)
        }

    @Test
    fun conditionalPressedColorResetsToUnspecified() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            if (pressed) color = Color.Red
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Unspecified, recorder.last.color)

            lateinit var press: PressInteraction.Press
            runBlocking {
                press = PressInteraction.Press(Offset.Zero)
                source.emit(press)
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)

            runBlocking { source.emit(PressInteraction.Release(press)) }
            waitForIdle()
            assertEquals(Color.Unspecified, recorder.last.color)
        }

    @Test
    fun conditionalAlphaShapeBorderAndAnimationSpecReset() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()
            val border = Border(width = 2.dp, color = Color.Green)
            val spec = spring<Float>()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            if (focused) {
                                alpha = 0.5f
                                shape = CircleShape
                                this.border = border
                                scaleAnimationSpec = spec
                            }
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(1f, recorder.last.alpha)
            assertSame(RectangleShape, recorder.last.shape)
            assertEquals(BorderDefaults.None, recorder.last.border)
            assertEquals(null, recorder.last.scaleAnimationSpec)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(0.5f, recorder.last.alpha)
            assertSame(CircleShape, recorder.last.shape)
            assertEquals(border, recorder.last.border)
            assertSame(spec, recorder.last.scaleAnimationSpec)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(1f, recorder.last.alpha)
            assertSame(RectangleShape, recorder.last.shape)
            assertEquals(BorderDefaults.None, recorder.last.border)
            assertEquals(null, recorder.last.scaleAnimationSpec)
        }

    @Test
    fun multiBranchBlockResolvesExpectedFinalBranch() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) {
                            when {
                                focused -> color = Color.Blue
                                pressed -> color = Color.Green
                            }
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Unspecified, recorder.last.color)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(Color.Blue, recorder.last.color)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(Color.Unspecified, recorder.last.color)

            lateinit var press: PressInteraction.Press
            runBlocking {
                press = PressInteraction.Press(Offset.Zero)
                source.emit(press)
            }
            waitForIdle()
            assertEquals(Color.Green, recorder.last.color)

            runBlocking { source.emit(PressInteraction.Release(press)) }
            waitForIdle()
            assertEquals(Color.Unspecified, recorder.last.color)
        }

    @Test
    fun interactionFlagsRemainCurrentWhileOutputsReset() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source, enabled = false, selected = true) {
                            if (focused) scale = 1.2f
                        }.recordStyle(recorder),
                )
            }
            waitForIdle()
            assertFalse(recorder.last.enabled)
            assertTrue(recorder.last.selected)
            assertFalse(recorder.last.focused)
            assertEquals(1f, recorder.last.scale)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertFalse(recorder.last.enabled)
            assertTrue(recorder.last.selected)
            assertTrue(recorder.last.focused)
            assertEquals(1.2f, recorder.last.scale)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertFalse(recorder.last.enabled)
            assertTrue(recorder.last.selected)
            assertFalse(recorder.last.focused)
            assertEquals(1f, recorder.last.scale)
        }

    @Test
    fun equalFinalOutputAfterBlockUpdateDoesNotRedispatch() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            var styleRevision by mutableStateOf(0)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(null) {
                            if (styleRevision >= 0) color = Color.Red
                        }
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            val initialUpdates = recorder.snapshots.size
            assertTrue(initialUpdates > 0)
            assertEquals(Color.Red, recorder.last.color)

            runOnIdle { styleRevision++ }
            waitForIdle()
            assertEquals(initialUpdates, recorder.snapshots.size)
            assertEquals(Color.Red, recorder.last.color)
        }

    @Test
    fun interactionInputsDispatchWhenFinalOutputIsUnchanged() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) { color = Color.Red }
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            val initialUpdates = recorder.snapshots.size
            assertFalse(recorder.last.focused)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(initialUpdates + 1, recorder.snapshots.size)
            assertTrue(recorder.last.focused)
            assertEquals(Color.Red, recorder.last.color)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(initialUpdates + 2, recorder.snapshots.size)
            assertFalse(recorder.last.focused)
            assertEquals(Color.Red, recorder.last.color)
        }

    @Test
    fun nestedScopesMaintainIndependentDefaults() =
        runComposeUiTest {
            val outerSource = MutableInteractionSource()
            val innerSource = MutableInteractionSource()
            val inner = StyleRecorder()
            lateinit var outerFocus: FocusInteraction.Focus
            lateinit var innerFocus: FocusInteraction.Focus

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(outerSource) {
                            if (focused) scale = 1.2f
                        },
                ) {
                    Box(
                        Modifier
                            .size(1.dp)
                            .interactionStyle(innerSource) {
                                color = Color.Blue
                                if (focused) scale = 1.5f
                            }.recordStyle(inner),
                    )
                }
            }
            waitForIdle()
            assertEquals(1f, inner.last.scale)
            assertEquals(Color.Blue, inner.last.color)

            runBlocking {
                outerFocus = FocusInteraction.Focus()
                outerSource.emit(outerFocus)
            }
            waitForIdle()
            assertEquals(1f, inner.last.scale)
            assertEquals(Color.Blue, inner.last.color)

            runBlocking {
                innerFocus = FocusInteraction.Focus()
                innerSource.emit(innerFocus)
            }
            waitForIdle()
            assertEquals(1.5f, inner.last.scale)
            assertEquals(Color.Blue, inner.last.color)

            runBlocking { innerSource.emit(FocusInteraction.Unfocus(innerFocus)) }
            waitForIdle()
            assertEquals(1f, inner.last.scale)
            assertEquals(Color.Blue, inner.last.color)
        }

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
                        else -> Color.Blue
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
            val updatesAfterDetach = recorder.snapshots.size

            runOnIdle { colorState.value = Color.Blue }
            waitForIdle()
            assertEquals(updatesAfterDetach, recorder.snapshots.size)

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
                color = if (focused) colorState.value else Color.Black
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
            assertEquals(Color.Black, recorder.last.color)
            val initialUpdates = recorder.snapshots.size

            runOnIdle {
                colorState.value = Color.Blue
                runBlocking { source.emit(FocusInteraction.Focus()) }
            }
            waitForIdle()

            assertTrue(recorder.last.focused)
            assertEquals(Color.Blue, recorder.last.color)
            assertTrue(
                recorder.snapshots.size <= initialUpdates + 2,
                "Expected at most 2 snapshot updates but got ${recorder.snapshots.size - initialUpdates}",
            )
        }
}

private data class SnapshotStyleBundle(
    val alpha: Float,
    val shape: Shape,
    val border: Border,
    val scale: Float,
    val scaleAnimationSpec: AnimationSpec<Float>?,
)

private data class StyleSnapshot(
    val enabled: Boolean,
    val selected: Boolean,
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
    val color: Color,
    val alpha: Float,
    val scale: Float,
    val shape: Shape,
    val border: Border,
    val scaleAnimationSpec: AnimationSpec<Float>?,
)

private class StyleRecorder {
    val snapshots = mutableListOf<StyleSnapshot>()
    val colors: List<Color>
        get() = snapshots.map { it.color }
    val last: StyleSnapshot
        get() = snapshots.last()
}

private fun Modifier.recordStyle(recorder: StyleRecorder): Modifier = this then RecordingStyleChildElement(recorder)

private data class RecordingStyleChildElement(
    val recorder: StyleRecorder,
) : ModifierNodeElement<RecordingStyleChildNode>() {
    override fun create() = RecordingStyleChildNode(recorder)

    override fun update(node: RecordingStyleChildNode) {
        node.recorder = recorder
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "RecordingStyleChildElement"
    }
}

private class RecordingStyleChildNode(
    var recorder: StyleRecorder,
) : Modifier.Node(), StyleScopeChildNode {
    override fun onAttach() {
        requestInitialStyleFromParent()
    }

    override fun onReset() {
        recorder.snapshots.clear()
    }

    override fun updateStyle(styleScope: StyleScope) {
        recorder.snapshots +=
            StyleSnapshot(
                enabled = styleScope.enabled,
                selected = styleScope.selected,
                focused = styleScope.focused,
                hovered = styleScope.hovered,
                pressed = styleScope.pressed,
                color = styleScope.color,
                alpha = styleScope.alpha,
                scale = styleScope.scale,
                shape = styleScope.shape,
                border = styleScope.border,
                scaleAnimationSpec = styleScope.scaleAnimationSpec,
            )
    }
}

private class InteractionRecorder(
    private val key: Any,
) {
    val states = mutableListOf<Interactions>()

    fun modifier(): Modifier = RecordingInteractionObserverElement(this, key)
}

private fun Modifier.recordInteractions(recorder: InteractionRecorder): Modifier = this then recorder.modifier()

private data class RecordingInteractionObserverElement(
    val recorder: InteractionRecorder,
    val key: Any,
) : ModifierNodeElement<RecordingInteractionObserverNode>() {
    override fun create() = RecordingInteractionObserverNode(recorder, key)

    override fun update(node: RecordingInteractionObserverNode) {
        node.recorder = recorder
        node.key = key
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "RecordingInteractionObserverElement"
    }
}

private class RecordingInteractionObserverNode(
    var recorder: InteractionRecorder,
    var key: Any,
) : Modifier.Node(), InteractionSourceObserverNode {
    override val traverseKey: Any
        get() = key

    override fun onInteractionStateChanged(interactions: Interactions) {
        recorder.states += interactions
    }
}
