// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalWildApi::class, ExperimentalTestApi::class)
class InteractionSourceElementTest {
    @Test
    fun overlappingPressesStayPressedUntilFinalRelease() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = TestInteractionRecorder()

            setContent {
                TestParentWithObservers(
                    interactionSource = source,
                    childTraversalKey = KeyA,
                    observerA = recorder,
                )
            }

            val press1 = PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero)
            val press2 = PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero)

            runOnIdle {
                assertTrue(source.tryEmit(press1))
                assertTrue(source.tryEmit(press2))
                assertTrue(source.tryEmit(PressInteraction.Release(press1)))
            }

            runOnIdle {
                assertEquals(listOf(Interactions(focused = false, hovered = false, pressed = true)), recorder.events)
                assertTrue(source.tryEmit(PressInteraction.Release(press2)))
            }

            runOnIdle {
                assertEquals(
                    listOf(
                        Interactions(focused = false, hovered = false, pressed = true),
                        Interactions(focused = false, hovered = false, pressed = false),
                    ),
                    recorder.events,
                )
            }
        }

    @Test
    fun overlappingHoverAndFocusStayActiveUntilTheirFinalTerminalInteractions() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = TestInteractionRecorder()

            setContent {
                TestParentWithObservers(
                    interactionSource = source,
                    childTraversalKey = KeyA,
                    observerA = recorder,
                )
            }

            val enter1 = HoverInteraction.Enter()
            val enter2 = HoverInteraction.Enter()
            val focus1 = FocusInteraction.Focus()
            val focus2 = FocusInteraction.Focus()

            runOnIdle {
                assertTrue(source.tryEmit(enter1))
                assertTrue(source.tryEmit(enter2))
                assertTrue(source.tryEmit(HoverInteraction.Exit(enter1)))
                assertTrue(source.tryEmit(focus1))
                assertTrue(source.tryEmit(focus2))
                assertTrue(source.tryEmit(FocusInteraction.Unfocus(focus1)))
            }

            runOnIdle {
                assertEquals(
                    listOf(
                        Interactions(focused = false, hovered = true, pressed = false),
                        Interactions(focused = true, hovered = true, pressed = false),
                    ),
                    recorder.events,
                )

                assertTrue(source.tryEmit(HoverInteraction.Exit(enter2)))
                assertTrue(source.tryEmit(FocusInteraction.Unfocus(focus2)))
            }

            runOnIdle {
                assertEquals(
                    listOf(
                        Interactions(focused = false, hovered = true, pressed = false),
                        Interactions(focused = true, hovered = true, pressed = false),
                        Interactions(focused = true, hovered = false, pressed = false),
                        Interactions(focused = false, hovered = false, pressed = false),
                    ),
                    recorder.events,
                )
            }
        }

    @Test
    fun unknownTerminalInteractionsDoNotNotify() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = TestInteractionRecorder()

            setContent {
                TestParentWithObservers(
                    interactionSource = source,
                    childTraversalKey = KeyA,
                    observerA = recorder,
                )
            }

            runOnIdle {
                assertTrue(source.tryEmit(PressInteraction.Release(PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero))))
                assertTrue(source.tryEmit(HoverInteraction.Exit(HoverInteraction.Enter())))
                assertTrue(source.tryEmit(FocusInteraction.Unfocus(FocusInteraction.Focus())))
            }

            runOnIdle {
                assertEquals(emptyList<Interactions>(), recorder.events)
            }
        }

    @Test
    fun sourceReplacementStopsOldSourceEventsAndDispatchesReset() =
        runComposeUiTest {
            val sourceA = MutableInteractionSource()
            val sourceB = MutableInteractionSource()
            val recorder = TestInteractionRecorder()
            var interactionSource by mutableStateOf<androidx.compose.foundation.interaction.InteractionSource?>(sourceA)

            setContent {
                TestParentWithObservers(
                    interactionSource = interactionSource,
                    childTraversalKey = KeyA,
                    observerA = recorder,
                )
            }

            val pressA = PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero)

            runOnIdle {
                assertTrue(sourceA.tryEmit(pressA))
            }

            runOnIdle {
                interactionSource = sourceB
            }

            runOnIdle {
                assertTrue(sourceA.tryEmit(PressInteraction.Release(pressA)))
                assertTrue(sourceB.tryEmit(PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero)))
            }

            runOnIdle {
                assertEquals(
                    listOf(
                        Interactions(focused = false, hovered = false, pressed = true),
                        Interactions(focused = false, hovered = false, pressed = false),
                        Interactions(focused = false, hovered = false, pressed = true),
                    ),
                    recorder.events,
                )
            }
        }

    @Test
    fun keyOnlyUpdateDispatchesCurrentStateToNewMatchingDescendants() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorderA = TestInteractionRecorder()
            val recorderB = TestInteractionRecorder()
            var childTraversalKey by mutableStateOf(KeyA as Any)

            setContent {
                TestParentWithObservers(
                    interactionSource = source,
                    childTraversalKey = childTraversalKey,
                    observerA = recorderA,
                    observerB = recorderB,
                )
            }

            val press = PressInteraction.Press(androidx.compose.ui.geometry.Offset.Zero)

            runOnIdle {
                assertTrue(source.tryEmit(press))
            }

            runOnIdle {
                childTraversalKey = KeyB
            }

            runOnIdle {
                assertTrue(source.tryEmit(PressInteraction.Release(press)))
            }

            runOnIdle {
                assertEquals(
                    listOf(Interactions(focused = false, hovered = false, pressed = true)),
                    recorderA.events,
                )
                assertEquals(
                    listOf(
                        Interactions(focused = false, hovered = false, pressed = true),
                        Interactions(focused = false, hovered = false, pressed = false),
                    ),
                    recorderB.events,
                )
            }
        }
}

@OptIn(ExperimentalWildApi::class)
@androidx.compose.runtime.Composable
private fun TestParentWithObservers(
    interactionSource: androidx.compose.foundation.interaction.InteractionSource?,
    childTraversalKey: Any,
    observerA: TestInteractionRecorder,
    observerB: TestInteractionRecorder? = null,
) {
    Box(
        modifier =
            Modifier.interactionSourceNode(
                interactionSource = interactionSource,
                childTraversalKey = childTraversalKey,
            ),
    ) {
        Box(modifier = Modifier.interactionObserverNode(KeyA, observerA))
        observerB?.let {
            Box(modifier = Modifier.interactionObserverNode(KeyB, it))
        }
    }
}

private class TestInteractionRecorder {
    val events = mutableListOf<Interactions>()
}

private fun Modifier.interactionObserverNode(
    key: Any,
    recorder: TestInteractionRecorder,
): Modifier = this then TestInteractionObserverElement(key, recorder)

private data class TestInteractionObserverElement(
    private val key: Any,
    private val recorder: TestInteractionRecorder,
) : ModifierNodeElement<InteractionObserverNode>() {
    override fun create(): InteractionObserverNode = InteractionObserverNode(key, recorder)

    override fun update(node: InteractionObserverNode) {
        node.key = key
        node.recorder = recorder
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "InteractionObserverElement"
    }
}

private class InteractionObserverNode(
    var key: Any,
    var recorder: TestInteractionRecorder,
) : Modifier.Node(),
    InteractionSourceObserverNode,
    TraversableNode {
    override fun onInteractionStateChanged(interactions: Interactions) {
        recorder.events += interactions
    }

    override val traverseKey: Any
        get() = key
}

private data object KeyA

private data object KeyB
