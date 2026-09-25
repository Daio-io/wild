// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ProgressIndicatorTest {
    @Test
    fun determinateLinearIndicatorNormalizesProgressForSemanticsAndContent() =
        runComposeUiTest {
            var contentProgress = Float.NaN

            setContent {
                LinearProgressIndicator(
                    progress = { 2f },
                    modifier = Modifier.testTag("progress"),
                ) { contentProgress = it }
            }

            assertEquals(
                ProgressBarRangeInfo(1f, 0f..1f),
                onNode(hasTestTag("progress")).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo],
            )
            runOnIdle { assertEquals(1f, contentProgress) }
        }

    @Test
    fun indeterminateLinearIndicatorExposesIndeterminateSemantics() =
        runComposeUiTest {
            setContent {
                LinearProgressIndicator(modifier = Modifier.testTag("progress")) {}
            }

            assertEquals(
                ProgressBarRangeInfo.Indeterminate,
                onNode(hasTestTag("progress")).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo],
            )
        }

    @Test
    fun determinateProgressCoercesBoundsAndNaNAndRecomposesFromState() =
        runComposeUiTest {
            var progress by mutableStateOf(-1f)
            var contentProgress = Float.NaN

            setContent {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.testTag("progress"),
                ) { contentProgress = it }
            }

            fun assertProgress(expected: Float) {
                assertEquals(
                    ProgressBarRangeInfo(expected, 0f..1f),
                    onNode(hasTestTag("progress")).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo],
                )
                assertEquals(expected, contentProgress)
            }

            assertProgress(0f)
            runOnIdle { progress = 0f }
            assertProgress(0f)
            runOnIdle { progress = 1f }
            assertProgress(1f)
            runOnIdle { progress = 2f }
            assertProgress(1f)
            runOnIdle { progress = Float.NaN }
            assertProgress(0f)
        }

    @Test
    fun circularIndicatorsExposeDeterminateAndIndeterminateSemantics() =
        runComposeUiTest {
            var circularContentProgress = Float.NaN

            setContent {
                androidx.compose.foundation.layout.Column {
                    CircularProgressIndicator(
                        progress = { 0.25f },
                        modifier = Modifier.testTag("determinate"),
                    ) { progress -> circularContentProgress = progress }
                    CircularProgressIndicator(modifier = Modifier.testTag("indeterminate")) {}
                }
            }

            assertEquals(
                ProgressBarRangeInfo(0.25f, 0f..1f),
                onNode(hasTestTag("determinate")).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo],
            )
            assertEquals(
                ProgressBarRangeInfo.Indeterminate,
                onNode(hasTestTag("indeterminate")).fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo],
            )
            runOnIdle { assertEquals(0.25f, circularContentProgress) }
        }

    @Test
    fun indicatorModifierRemainsOnSingleSemanticNode() =
        runComposeUiTest {
            setContent {
                LinearProgressIndicator(
                    progress = { 0.5f },
                    modifier = Modifier.testTag("progress"),
                ) {}
            }

            assertEquals(1, onAllNodes(hasTestTag("progress")).fetchSemanticsNodes().size)
        }
}
