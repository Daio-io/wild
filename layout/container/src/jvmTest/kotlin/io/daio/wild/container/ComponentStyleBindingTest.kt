// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.styleSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ComponentStyleBindingTest {
    @Test
    fun publishResolvedColor_skipsEqualColorAssignment() {
        val binding = ComponentStyleBinding(initial = Color.Red)
        val before = binding.contentColor
        binding.publishResolvedColor(Color.Red)
        assertEquals(Color.Red, binding.contentColor.value)
        assertTrue(before === binding.contentColor)

        binding.publishResolvedColor(Color.Blue)
        assertEquals(Color.Blue, binding.contentColor.value)
    }

    @Test
    fun firstRenderedFrame_enabledUnfocused_usesInitialContentColor() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var firstFrameColor = Color.Unspecified
            var frameCount = 0

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.size(48.dp),
                    style = contentStyle(unfocused = Color.Red, focused = Color.Green),
                ) {
                    val color = LocalContentColor.current
                    SideEffect {
                        if (frameCount == 0) {
                            firstFrameColor = color
                        }
                        frameCount++
                    }
                }
            }

            mainClock.advanceTimeByFrame()
            runOnIdle {
                assertEquals(Color.Red, firstFrameColor)
                assertTrue(frameCount >= 1)
            }
        }

    @Test
    fun firstRenderedFrame_disabled_usesDisabledContentColor() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var firstFrameColor = Color.Unspecified

            setContent {
                Container(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.size(48.dp),
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    contentColor = Color.Red,
                                    disabledContentColor = Color.Gray,
                                    focusedContentColor = Color.Green,
                                ),
                        ),
                ) {
                    val color = LocalContentColor.current
                    SideEffect {
                        if (firstFrameColor == Color.Unspecified) {
                            firstFrameColor = color
                        }
                    }
                }
            }

            mainClock.advanceTimeByFrame()
            runOnIdle { assertEquals(Color.Gray, firstFrameColor) }
        }

    @Test
    fun firstRenderedFrame_specCallbackOverride_usesResolvedContentColor() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            var firstFrameColor = Color.Unspecified
            val spec =
                styleSpec(
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                contentColor = Color.Red,
                                disabledContentColor = Color.Red,
                                focusedContentColor = Color.Green,
                            ),
                    ),
                ) {
                    contentColor = if (enabled) Color.White else Color.Gray
                }

            setContent {
                Container(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.size(48.dp),
                    style = spec,
                ) {
                    val color = LocalContentColor.current
                    SideEffect {
                        if (firstFrameColor == Color.Unspecified) {
                            firstFrameColor = color
                        }
                    }
                }
            }

            mainClock.advanceTimeByFrame()
            runOnIdle { assertEquals(Color.Gray, firstFrameColor) }
        }

    @Test
    fun equalResolvedColor_interactionChange_noContentRecompose() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            var contentCompositions = 0
            // Same content color for focused and unfocused — interaction must not recompose content.
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            contentColor = Color.Red,
                            focusedContentColor = Color.Red,
                            backgroundColor = Color.Black,
                            focusedBackgroundColor = Color.Blue,
                        ),
                )

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.testTag("container").size(48.dp),
                    style = style,
                    interactionSource = source,
                ) {
                    contentCompositions++
                    LocalContentColor.current
                }
            }

            waitForIdle()
            val afterInitial = contentCompositions

            runOnIdle { assertTrue(source.tryEmit(FocusInteraction.Focus())) }
            waitForIdle()

            assertEquals(afterInitial, contentCompositions)
        }

    @Test
    fun equalResolvedColor_focusChange_updatesWhenColorDiffers() =
        runComposeUiTest {
            var observed = Color.Unspecified

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.testTag("container").size(48.dp),
                    style = contentStyle(unfocused = Color.Red, focused = Color.Green),
                ) {
                    observed = LocalContentColor.current
                }
            }

            runOnIdle { assertEquals(Color.Red, observed) }
            onNodeWithTag("container")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()
            runOnIdle { assertEquals(Color.Green, observed) }
        }

    @Test
    fun styleOnlySnapshotReads_notSubscribedInComposition() =
        runComposeUiTest {
            var chromeTick by mutableIntStateOf(0)
            var contentCompositions = 0
            val spec =
                styleSpec(
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                contentColor = Color.White,
                                focusedContentColor = Color.White,
                                backgroundColor = Color.Black,
                            ),
                    ),
                ) {
                    color = if (chromeTick % 2 == 0) Color.Black else Color.DarkGray
                    contentColor = Color.White
                }

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.size(48.dp),
                    style = spec,
                ) {
                    contentCompositions++
                    LocalContentColor.current
                }
            }

            waitForIdle()
            val afterInitial = contentCompositions

            repeat(3) {
                runOnIdle { chromeTick++ }
                waitForIdle()
            }

            assertEquals(afterInitial, contentCompositions)
        }

    @Test
    fun specOverload_publishesCallbackContentColorOnFocus() =
        runComposeUiTest {
            var observed = Color.Unspecified
            val spec =
                styleSpec(
                    StyleDefaults.style(
                        colors =
                            StyleDefaults.colors(
                                contentColor = Color.Red,
                                focusedContentColor = Color.Red,
                            ),
                    ),
                ) {
                    if (focused) contentColor = Color.Yellow
                }

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.testTag("container").size(48.dp),
                    style = spec,
                ) {
                    observed = LocalContentColor.current
                }
            }

            runOnIdle { assertEquals(Color.Red, observed) }
            onNodeWithTag("container")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()
            runOnIdle { assertEquals(Color.Yellow, observed) }
        }
}

private fun contentStyle(
    unfocused: Color,
    focused: Color,
) = StyleDefaults.style(
    colors =
        StyleDefaults.colors(
            contentColor = unfocused,
            focusedContentColor = focused,
        ),
)
