// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.Style
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleScope
import io.daio.wild.style.interactionStyle
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the THE-217 [InteractionStyleNode] prototype. These verify the candidate resolves
 * style the same way as the current traversal chain, skips delegate updates (and therefore
 * `invalidateDraw`/`invalidatePlacement`) when a re-resolution produces an identical output, keeps
 * nested candidates isolated, and never relies on style-parent-to-child traversal.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class InteractionStyleNodeTest {
    private val style =
        StyleDefaults.style(
            colors =
                StyleDefaults.colors(
                    backgroundColor = Color.Black,
                    focusedBackgroundColor = Color.Blue,
                    hoveredBackgroundColor = Color.Cyan,
                    pressedBackgroundColor = Color.Red,
                    disabledBackgroundColor = Color.Gray,
                    selectedBackgroundColor = Color.Yellow,
                    focusedSelectedBackgroundColor = Color.Magenta,
                ),
            scale =
                StyleDefaults.scale(
                    scale = 1f,
                    focusedScale = 1.1f,
                    hoveredScale = 1.2f,
                    pressedScale = 1.3f,
                    selectedScale = 1.4f,
                    disabledScale = 0.9f,
                ),
        )

    @Test
    fun valuePathResolvesColorAndScaleAcrossInteractionStates() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode
            val source = MutableInteractionSource()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = source, style = style) { node = it },
                )
            }
            waitForIdle()
            assertEquals(Color.Black, node.color)
            assertEquals(1f, node.scale)

            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()
            assertEquals(Color.Blue, node.color)
            assertEquals(1.1f, node.scale)
        }

    @Test
    fun valuePathResolvesEnabledAndSelectedStateOnFirstAttachedFrame() =
        runComposeUiTest {
            lateinit var disabledNode: InteractionStyleNode
            lateinit var selectedNode: InteractionStyleNode

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(
                            interactionSource = null,
                            enabled = false,
                            style = style,
                        ) { disabledNode = it },
                )
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(
                            interactionSource = null,
                            selected = true,
                            style = style,
                        ) { selectedNode = it },
                )
            }
            waitForIdle()
            assertEquals(Color.Gray, disabledNode.color)
            assertFalse(disabledNode.enabled)
            assertEquals(Color.Yellow, selectedNode.color)
            assertTrue(selectedNode.selected)
        }

    @Test
    fun blockPathResetsOutputsBetweenEvaluationsPerThe219() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode
            val source = MutableInteractionSource()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = source, block = {
                            if (focused) scale = 1.5f
                        }) { node = it },
                )
            }
            waitForIdle()
            assertEquals(1f, node.scale)

            lateinit var focus: FocusInteraction.Focus
            runBlocking {
                focus = FocusInteraction.Focus()
                source.emit(focus)
            }
            waitForIdle()
            assertEquals(1.5f, node.scale)

            runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
            waitForIdle()
            assertEquals(1f, node.scale)
        }

    @Test
    fun blockPathObservesSnapshotStateReadsPerThe225() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode
            val colorState = mutableStateOf(Color.Red)
            val stableBlock: StyleScope.() -> Unit = { color = colorState.value }

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = null, block = stableBlock) { node = it },
                )
            }
            waitForIdle()
            assertEquals(Color.Red, node.color)

            runOnIdle { colorState.value = Color.Blue }
            waitForIdle()
            assertEquals(Color.Blue, node.color)
        }

    @Test
    fun equalResolvedOutputDoesNotDispatchToAnyDelegate() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode
            var revision by mutableStateOf(0)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = null, block = {
                            if (revision >= 0) color = Color.Red
                        }) { node = it },
                )
            }
            waitForIdle()
            val dispatchesAfterAttach = node.dispatchCount
            val scaleCallsAfterAttach = node.scaleNode.updateCallCountForTest
            val contentCallsAfterAttach = node.backgroundBorderNode.contentUpdateCallCountForTest
            val backgroundCallsAfterAttach = node.backgroundBorderNode.backgroundUpdateCallCountForTest
            val borderCallsAfterAttach = node.backgroundBorderNode.borderUpdateCallCountForTest
            assertTrue(dispatchesAfterAttach > 0)

            // Forces the block's identity to change (new closure per recomposition) and the block
            // to be re-evaluated, but resolves to the exact same color as before.
            runOnIdle { revision++ }
            waitForIdle()

            assertEquals(dispatchesAfterAttach, node.dispatchCount)
            assertEquals(scaleCallsAfterAttach, node.scaleNode.updateCallCountForTest)
            assertEquals(contentCallsAfterAttach, node.backgroundBorderNode.contentUpdateCallCountForTest)
            assertEquals(backgroundCallsAfterAttach, node.backgroundBorderNode.backgroundUpdateCallCountForTest)
            assertEquals(borderCallsAfterAttach, node.backgroundBorderNode.borderUpdateCallCountForTest)
            assertEquals(Color.Red, node.color)
        }

    @Test
    fun changeMaskOnlyDispatchesToTheDelegateAffectedByTheChange() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode
            val source = MutableInteractionSource()

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = source, block = {
                            // Only scale reacts to focus; color/shape/border stay constant.
                            color = Color.Red
                            if (focused) scale = 1.2f
                        }) { node = it },
                )
            }
            waitForIdle()
            val backgroundCallsBefore = node.backgroundBorderNode.backgroundUpdateCallCountForTest
            val borderCallsBefore = node.backgroundBorderNode.borderUpdateCallCountForTest
            val contentCallsBefore = node.backgroundBorderNode.contentUpdateCallCountForTest
            val scaleCallsBefore = node.scaleNode.updateCallCountForTest

            runBlocking { source.emit(FocusInteraction.Focus()) }
            waitForIdle()

            assertEquals(1.2f, node.scale)
            assertTrue(node.scaleNode.updateCallCountForTest > scaleCallsBefore)
            assertEquals(backgroundCallsBefore, node.backgroundBorderNode.backgroundUpdateCallCountForTest)
            assertEquals(borderCallsBefore, node.backgroundBorderNode.borderUpdateCallCountForTest)
            assertEquals(contentCallsBefore, node.backgroundBorderNode.contentUpdateCallCountForTest)
        }

    @Test
    fun nestedCandidateStylesResolveIndependently() =
        runComposeUiTest {
            lateinit var outerNode: InteractionStyleNode
            lateinit var innerNode: InteractionStyleNode
            var outerEnabled by mutableStateOf(true)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = null, enabled = outerEnabled, block = {
                            color = if (enabled) Color.Red else Color.Green
                        }) { outerNode = it },
                ) {
                    Box(
                        Modifier
                            .size(1.dp)
                            .captureInteractionStyleNode(interactionSource = null, block = {
                                color = Color.Blue
                            }) { innerNode = it },
                    )
                }
            }
            waitForIdle()
            assertEquals(Color.Red, outerNode.color)
            assertEquals(Color.Blue, innerNode.color)
            val innerDispatchesBefore = innerNode.dispatchCount

            runOnIdle { outerEnabled = false }
            waitForIdle()

            assertEquals(Color.Green, outerNode.color)
            assertEquals(Color.Blue, innerNode.color)
            assertEquals(innerDispatchesBefore, innerNode.dispatchCount)
        }

    @Test
    fun candidateNodeDoesNotImplementTraversableNode() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .captureInteractionStyleNode(interactionSource = null, block = { color = Color.Red }) {
                            node = it
                        },
                )
            }
            waitForIdle()

            assertFalse((node as Any) is TraversableNode)
        }

    @Test
    fun candidateIsUnreachableByAnAncestorRealStyleParentTraversal() =
        runComposeUiTest {
            lateinit var node: InteractionStyleNode

            setContent {
                Box(
                    // A real traversal-based style parent, used only to prove the candidate below
                    // is not discoverable as one of its StyleScopeChildNode descendants.
                    Modifier
                        .size(1.dp)
                        .interactionStyle(interactionSource = null) { color = Color.Red },
                ) {
                    Box(
                        Modifier
                            .size(1.dp)
                            .captureInteractionStyleNode(interactionSource = null, block = {
                                color = Color.Blue
                            }) { node = it },
                    )
                }
            }
            waitForIdle()

            assertEquals(Color.Blue, node.color)
        }
}

@OptIn(ExperimentalWildApi::class)
private fun Modifier.captureInteractionStyleNode(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: Style,
    onNode: (InteractionStyleNode) -> Unit,
): Modifier =
    this then
        TestInteractionStyleElement(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = StyleResolver.Value(style),
            onNode = onNode,
        )

@OptIn(ExperimentalWildApi::class)
private fun Modifier.captureInteractionStyleNode(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    selected: Boolean = false,
    block: StyleScope.() -> Unit,
    onNode: (InteractionStyleNode) -> Unit,
): Modifier =
    this then
        TestInteractionStyleElement(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = StyleResolver.Block(block),
            onNode = onNode,
        )

private data class TestInteractionStyleElement(
    private val interactionSource: InteractionSource?,
    private val enabled: Boolean,
    private val selected: Boolean,
    private val resolver: StyleResolver,
    private val onNode: (InteractionStyleNode) -> Unit,
) : ModifierNodeElement<InteractionStyleNode>() {
    override fun create(): InteractionStyleNode =
        InteractionStyleNode(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = resolver,
        ).also(onNode)

    override fun update(node: InteractionStyleNode) {
        node.updateState(
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            resolver = resolver,
        )
        onNode(node)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "TestInteractionStyleElement"
    }
}
