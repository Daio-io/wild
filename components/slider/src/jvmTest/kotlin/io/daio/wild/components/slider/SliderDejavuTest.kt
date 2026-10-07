// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
class SliderDejavuTest {
    @Test
    fun slider_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                Slider(
                    value = 0.25f,
                    onValueChange = {},
                    modifier = Modifier.testTag("sut").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun slider_valueChange_recomposesTwice() =
        runRecompositionTrackingUiTest {
            var value by mutableStateOf(0.25f)
            setTrackedContent {
                Slider(
                    value = value,
                    onValueChange = {},
                    modifier = Modifier.testTag("sut").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { value = 0.75f }
            waitForIdle()
            onNodeWithTag("sut").assertRecompositions(exactly = 2)
        }
}
