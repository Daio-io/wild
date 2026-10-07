// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

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
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ContainerDejavuTest {
    @Test
    fun staticContainer_unrelatedTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                TaggedStaticContainer(color = Color.Gray)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("container").assertStable()
        }

    @Test
    fun staticContainer_colorChange_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var color by mutableStateOf(Color.Gray)
            setTrackedContent {
                TaggedStaticContainer(color = color)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { color = Color.Blue }
            waitForIdle()
            onNodeWithTag("container").assertRecompositions(exactly = 1)
        }

    @Test
    fun interactiveContainer_unrelatedTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                TaggedInteractiveContainer()
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("container").assertStable()
        }
}

/**
 * User composable owning the SUT tag. Dejavu skips framework layout nodes, so the tag must sit on
 * a skippable user boundary to measure static [Container] stability under parent invalidation.
 */
@Composable
private fun TaggedStaticContainer(color: Color) {
    Container(modifier = Modifier.testTag("container").size(8.dp), color = color) {}
}

/**
 * User composable owning the SUT tag for the interactive [Container] overload.
 */
@Composable
private fun TaggedInteractiveContainer() {
    Container(
        onClick = {},
        modifier = Modifier.testTag("container").size(8.dp),
        style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Gray)),
    ) {}
}
