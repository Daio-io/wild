// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
class ProgressIndicatorDejavuTest {
    @Test
    fun linearProgress_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            var p by mutableFloatStateOf(0f)
            setTrackedContent {
                tick
                LinearProgressIndicator(progress = { p }, modifier = Modifier.testTag("sut"))
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun linearProgress_progressChange_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var p by mutableFloatStateOf(0f)
            setTrackedContent {
                LinearProgressIndicator(progress = { p }, modifier = Modifier.testTag("sut"))
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { p = 0.5f }
            waitForIdle()
            onNodeWithTag("sut").assertRecompositions(exactly = 1)
        }
}
