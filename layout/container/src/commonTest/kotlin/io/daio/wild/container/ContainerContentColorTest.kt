// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.StyleDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ContainerContentColorTest {
    @Test
    fun interactiveContainer_usesSelectedContentColorFromParameter() =
        runComposeUiTest {
            var contentColor by mutableStateOf(Color.Unspecified)

            setContent {
                Container(
                    onClick = {},
                    selected = true,
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    contentColor = Color.Black,
                                    selectedContentColor = Color.Green,
                                ),
                        ),
                ) {
                    contentColor = LocalContentColor.current
                }
            }

            waitForIdle()
            assertEquals(Color.Green, contentColor)
        }

    @Test
    fun interactiveContainer_updatesContentColorWhenFocused() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val focusRequester = FocusRequester()
            var contentColor by mutableStateOf(Color.Unspecified)

            setContent {
                Container(
                    onClick = {},
                    interactionSource = source,
                    modifier =
                        Modifier
                            .focusRequester(focusRequester)
                            .size(8.dp),
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    contentColor = Color.Black,
                                    focusedContentColor = Color.Red,
                                ),
                        ),
                ) {
                    contentColor = LocalContentColor.current
                }
            }

            waitForIdle()
            assertEquals(Color.Black, contentColor)

            runOnIdle { focusRequester.requestFocus() }
            waitForIdle()
            assertEquals(Color.Red, contentColor)
        }

    @Test
    fun interactiveContainer_updatesContentColorWhenPressed() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            lateinit var scope: CoroutineScope
            lateinit var press: PressInteraction.Press
            var contentColor by mutableStateOf(Color.Unspecified)

            setContent {
                scope = rememberCoroutineScope()
                Container(
                    onClick = {},
                    interactionSource = source,
                    modifier = Modifier.size(8.dp),
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    contentColor = Color.Black,
                                    pressedContentColor = Color.Blue,
                                ),
                        ),
                ) {
                    contentColor = LocalContentColor.current
                }
            }

            waitForIdle()
            assertEquals(Color.Black, contentColor)

            runOnIdle {
                scope.launch {
                    press = PressInteraction.Press(Offset.Zero)
                    source.emit(press)
                }
            }
            waitForIdle()
            assertEquals(Color.Blue, contentColor)

            runOnIdle {
                scope.launch { source.emit(PressInteraction.Release(press)) }
            }
            waitForIdle()
            assertEquals(Color.Black, contentColor)
        }

    @Test
    fun interactiveContainer_updatesContentColorWhenHovered() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            lateinit var scope: CoroutineScope
            lateinit var hoverEnter: HoverInteraction.Enter
            var contentColor by mutableStateOf(Color.Unspecified)

            setContent {
                scope = rememberCoroutineScope()
                Container(
                    onClick = {},
                    interactionSource = source,
                    modifier = Modifier.size(8.dp),
                    style =
                        StyleDefaults.style(
                            colors =
                                StyleDefaults.colors(
                                    contentColor = Color.Black,
                                    hoveredContentColor = Color.Yellow,
                                ),
                        ),
                ) {
                    contentColor = LocalContentColor.current
                }
            }

            waitForIdle()
            assertEquals(Color.Black, contentColor)

            runOnIdle {
                scope.launch {
                    hoverEnter = HoverInteraction.Enter()
                    source.emit(hoverEnter)
                }
            }
            waitForIdle()
            assertEquals(Color.Yellow, contentColor)

            runOnIdle {
                scope.launch { source.emit(HoverInteraction.Exit(hoverEnter)) }
            }
            waitForIdle()
            assertEquals(Color.Black, contentColor)
        }
}
