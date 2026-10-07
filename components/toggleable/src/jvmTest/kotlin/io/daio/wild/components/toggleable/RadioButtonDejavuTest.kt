// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import dejavu.assertRecompositions
import dejavu.assertStable
import dejavu.resetRecompositionCounts
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class RadioButtonDejavuTest {
    @Test
    fun radio_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                RadioButton(
                    selected = false,
                    onClick = {},
                    modifier = Modifier.testTag("sut"),
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun radio_selectedFlip_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var selected by mutableStateOf(false)
            setTrackedContent {
                RadioButton(
                    selected = selected,
                    onClick = {},
                    modifier = Modifier.testTag("sut"),
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { selected = true }
            waitForIdle()
            onNodeWithTag("sut").assertRecompositions(exactly = 1)
        }
}
