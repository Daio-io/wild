// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
class SwitchDejavuTest {
    @Test
    fun switch_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                Switch(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("sut"),
                ) {
                    Box(Modifier.size(8.dp))
                }
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun switch_checkedFlip_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var checked by mutableStateOf(false)
            setTrackedContent {
                Switch(
                    checked = checked,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("sut"),
                ) {
                    Box(Modifier.size(8.dp))
                }
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { checked = true }
            waitForIdle()
            onNodeWithTag("sut").assertRecompositions(exactly = 1)
        }
}
