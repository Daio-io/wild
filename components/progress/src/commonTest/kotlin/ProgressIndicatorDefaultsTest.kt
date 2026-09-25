// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ProgressIndicatorDefaultsTest {
    @Test
    fun defaultLinearTrackHeightIs4dp() {
        assertEquals(4.0, ProgressIndicatorDefaults.LinearTrackHeight.value.toDouble())
    }

    @Test
    fun defaultCircularSizeAndStrokeMatchOpinions() {
        assertEquals(40.0, ProgressIndicatorDefaults.CircularSize.value.toDouble())
        assertEquals(4.0, ProgressIndicatorDefaults.CircularStrokeWidth.value.toDouble())
        assertEquals(StrokeCap.Round, ProgressIndicatorDefaults.DefaultStrokeCap)
    }

    @Test
    fun linearIndicatorComposesAtCustomStrokeHeight() =
        runComposeUiTest {
            setContent {
                ProgressIndicatorDefaults.LinearIndicator(
                    progress = 0.5f,
                    modifier = Modifier.testTag("indicator"),
                    strokeHeight = 8.dp,
                )
            }

            onNodeWithTag("indicator").assertIsDisplayed()
        }

    @Test
    fun circularIndicatorComposesAtCustomStrokeWidth() =
        runComposeUiTest {
            setContent {
                ProgressIndicatorDefaults.CircularIndicator(
                    progress = 0.5f,
                    modifier = Modifier.testTag("indicator"),
                    strokeWidth = 8.dp,
                )
            }

            onNodeWithTag("indicator").assertIsDisplayed()
        }

    @Test
    fun customContentReplacesDefaultIndicator() =
        runComposeUiTest {
            var receivedProgress = Float.NaN

            setContent {
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier.testTag("progress"),
                ) { progress ->
                    receivedProgress = progress
                    ProgressIndicatorDefaults.LinearIndicator(
                        progress = progress,
                        strokeHeight = 10.dp,
                    )
                }
            }

            onNodeWithTag("progress").assertIsDisplayed()
            runOnIdle { assertEquals(0.5f, receivedProgress) }
        }
}
