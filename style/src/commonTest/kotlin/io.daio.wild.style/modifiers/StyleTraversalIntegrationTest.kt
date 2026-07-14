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
    // Also covers Task 2 skip-dispatch when final resolved output is unchanged.
    fun equalFinalOutputAfterResetDoesNotRedispatch() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()
            lateinit var focus: FocusInteraction.Focus

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .interactionStyle(source) { color = Color.Red }
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(1, recorder.snapshots.size)
            assertEquals(Color.Red, recorder.last.color)

            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(1, recorder.snapshots.size)
            assertEquals(Color.Red, recorder.last.color)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(1, recorder.snapshots.size)
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
}

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
