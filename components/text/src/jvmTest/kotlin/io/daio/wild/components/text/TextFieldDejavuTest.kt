// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
class TextFieldDejavuTest {
    @Test
    fun textField_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                TextField(
                    remember { TextFieldState("a") },
                    modifier = Modifier.testTag("sut"),
                    cursorBrush = SolidColor(Color.Black),
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun textField_edit_recomposesZero() =
        runRecompositionTrackingUiTest {
            val state = TextFieldState("a")
            setTrackedContent {
                TextField(
                    state,
                    modifier = Modifier.testTag("sut"),
                    cursorBrush = SolidColor(Color.Black),
                )
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { state.edit { replace(0, length, "b") } }
            waitForIdle()
            // budget: 0 — TextFieldState edits are observed inside BasicTextField
            onNodeWithTag("sut").assertRecompositions(exactly = 0)
        }
}
