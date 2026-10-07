// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import dejavu.assertRecompositions
import dejavu.assertStable
import dejavu.resetRecompositionCounts
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class InteractionStyleRecompositionTest {
    @Test
    fun interactionStyle_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            val style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                TaggedStyledBox(style = style)
            }
            waitForIdle()
            resetRecompositionCounts() // AFTER initial composition — counts exclude first compose
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("styled_box").assertStable()
        }

    @Test
    fun interactionStyle_colorChange_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var style by mutableStateOf(
                StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red)),
            )
            setTrackedContent {
                TaggedStyledBox(style = style)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle {
                style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Green))
            }
            waitForIdle()
            onNodeWithTag("styled_box").assertRecompositions(exactly = 1)
            // If actual > 1: change to exactly = N with // budget: N — do not edit production Style APIs
        }

    @Test
    fun interactionStyle_enabledFlip_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var enabled by mutableStateOf(true)
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = Color.Red,
                            disabledBackgroundColor = Color.Gray,
                        ),
                )
            setTrackedContent {
                TaggedStyledBox(style = style, enabled = enabled)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { enabled = false }
            waitForIdle()
            onNodeWithTag("styled_box").assertRecompositions(exactly = 1)
        }

    @Test
    fun interactionStyle_focusEmit_recomposesOnce() =
        runRecompositionTrackingUiTest {
            val source = MutableInteractionSource()
            val style =
                StyleDefaults.style(
                    colors =
                        StyleDefaults.colors(
                            backgroundColor = Color.Red,
                            focusedBackgroundColor = Color.Blue,
                        ),
                )
            setTrackedContent {
                TaggedStyledBox(style = style, interactionSource = source)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { source.tryEmit(FocusInteraction.Focus()) }
            waitForIdle()
            // modifier-node; composable budget 0
            onNodeWithTag("styled_box").assertStable()
        }
}

/**
 * User composable owning the SUT tag. Dejavu skips framework [Box], so the tag must sit on a
 * skippable user boundary to measure [interactionStyle] stability under parent invalidation.
 */
@Composable
private fun TaggedStyledBox(
    style: Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    Box(
        Modifier
            .testTag("styled_box")
            .size(8.dp)
            .interactionStyle(
                interactionSource = interactionSource,
                enabled = enabled,
                style = style,
            ),
    )
}
