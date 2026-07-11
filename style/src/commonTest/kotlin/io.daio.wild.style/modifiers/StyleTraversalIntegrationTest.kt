// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Border
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
                        .interactionStyle(null) { color = Color.Red }
                        .recordStyle(first)
                        .recordStyle(second),
                )
            }

            waitForIdle()
            assertEquals(listOf(Color.Red), first.colors)
            assertEquals(listOf(Color.Red), second.colors)
        }

    @Test
    fun childAttachedAfterParentRequestsCurrentStyle() =
        runComposeUiTest {
            val recorder = StyleRecorder()
            var showChild by mutableStateOf(false)

            setContent {
                Box(
                    Modifier
                        .interactionStyle(null) { color = Color.Green }
                        .then(if (showChild) Modifier.recordStyle(recorder) else Modifier),
                )
            }
            waitForIdle()

            showChild = true
            waitForIdle()

            assertEquals(listOf(Color.Green), recorder.colors)
        }

    @Test
    fun initiallyDisabledAndSelectedSurfacesResolveOnFirstAttachedFrame() =
        runComposeUiTest {
            val disabledRecorder = StyleRecorder()
            val selectedRecorder = StyleRecorder()

            setContent {
                Box(
                    Modifier
                        .interactionStyle(null, enabled = false) {
                            color = if (!enabled) Color.Gray else Color.Red
                        }.recordStyle(disabledRecorder),
                )
                Box(
                    Modifier
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
            assertEquals(1, recorder.snapshots.size)

            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(2, recorder.snapshots.size)
            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()
            assertEquals(2, recorder.snapshots.size)

            runBlocking {
                hover = HoverInteraction.Enter()
                source.emit(hover)
            }
            waitForIdle()
            assertEquals(3, recorder.snapshots.size)
            runBlocking {
                press = PressInteraction.Press(Offset.Zero)
                source.emit(press)
            }
            waitForIdle()
            assertEquals(4, recorder.snapshots.size)
            runBlocking { source.emit(PressInteraction.Release(press)) }
            waitForIdle()
            assertEquals(5, recorder.snapshots.size)
            runBlocking {
                source.emit(HoverInteraction.Exit(hover))
                source.emit(FocusInteraction.Unfocus(focus))
            }
            waitForIdle()
            assertEquals(7, recorder.snapshots.size)
        }

    @Test
    fun nestedStyleParentsPreventOuterUpdatesReachingInnerChildren() =
        runComposeUiTest {
            val outer = StyleRecorder()
            val inner = StyleRecorder()
            var outerColor by mutableStateOf(Color.Red)

            setContent {
                Box(
                    Modifier
                        .interactionStyle(null) { color = outerColor }
                        .recordStyle(outer),
                ) {
                    Box(
                        Modifier
                            .interactionStyle(null) { color = Color.Blue }
                            .recordStyle(inner),
                    )
                }
            }
            waitForIdle()
            assertEquals(Color.Red, outer.last.color)
            assertEquals(Color.Blue, inner.last.color)
            val innerUpdates = inner.snapshots.size

            outerColor = Color.Green
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
            var firstColor by mutableStateOf(Color.Red)

            setContent {
                Box(Modifier.interactionStyle(null) { color = firstColor }.recordStyle(first))
                Box(Modifier.interactionStyle(null) { color = Color.Blue }.recordStyle(second))
            }
            waitForIdle()
            val secondUpdates = second.snapshots.size

            firstColor = Color.Green
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
            var color by mutableStateOf(Color.Red)

            setContent {
                Box(
                    Modifier
                        .interactionStyle(null) { this.color = color }
                        .then(if (showChild) Modifier.recordStyle(recorder) else Modifier),
                )
            }
            waitForIdle()
            showChild = false
            waitForIdle()
            val updatesAfterDetach = recorder.snapshots.size

            color = Color.Blue
            waitForIdle()
            assertEquals(updatesAfterDetach, recorder.snapshots.size)

            showChild = true
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

            source = sourceB
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
                        .interactionSourceNode(source, childKey)
                        .recordInteractions(oldObserver)
                        .recordInteractions(newObserver),
                )
            }
            waitForIdle()
            oldObserver.states.clear()
            newObserver.states.clear()

            childKey = newKey
            waitForIdle()
            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()

            assertEquals(1, newObserver.states.size)
            assertTrue(newObserver.states.single().focused)
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
