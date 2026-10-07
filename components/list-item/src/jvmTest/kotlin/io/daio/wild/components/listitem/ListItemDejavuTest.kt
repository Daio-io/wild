// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import androidx.compose.foundation.text.BasicText
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
class ListItemDejavuTest {
    @Test
    fun listItem_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                ListItem(onClick = {}, modifier = Modifier.testTag("sut")) { BasicText("row") }
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun listItem_enabledChange_recomposesOnce() =
        runRecompositionTrackingUiTest {
            var enabled by mutableStateOf(true)
            setTrackedContent {
                ListItem(
                    onClick = {},
                    enabled = enabled,
                    modifier = Modifier.testTag("sut"),
                ) {
                    BasicText("row")
                }
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { enabled = false }
            waitForIdle()
            onNodeWithTag("sut").assertRecompositions(exactly = 1)
        }
}
