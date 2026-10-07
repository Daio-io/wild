// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import androidx.compose.foundation.background
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
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ProvidesContentColorDejavuTest {
    @Test
    fun providesContentColor_unrelatedTick_childStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            var color by mutableStateOf(Color.Red)
            setTrackedContent {
                tick
                TaggedContentColorChild(color = color)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("child").assertStable()
        }

    @Test
    fun providesContentColor_colorChange_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var color by mutableStateOf(Color.Red)
            setTrackedContent {
                TaggedContentColorChild(color = color)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { color = Color.Blue }
            waitForIdle()
            onNodeWithTag("child").assertRecompositions(exactly = 1)
        }
}

/**
 * User composable owning the SUT tag. Dejavu skips framework [Box], so the tag must sit on a
 * skippable user boundary to measure [ProvidesContentColor] stability under parent invalidation.
 */
@Composable
private fun TaggedContentColorChild(color: Color) {
    ProvidesContentColor(color) {
        Box(Modifier.testTag("child").size(8.dp).background(LocalContentColor.current))
    }
}
