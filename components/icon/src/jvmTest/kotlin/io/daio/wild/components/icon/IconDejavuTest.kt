// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
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

private val testVector =
    ImageVector.Builder(
        name = "VisualTestVector",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(12f, 2f)
            lineTo(22f, 12f)
            lineTo(12f, 22f)
            lineTo(2f, 12f)
            close()
        }
    }.build()

@OptIn(ExperimentalTestApi::class)
class IconDejavuTest {
    @Test
    fun icon_unrelatedParentTick_isStable() =
        runRecompositionTrackingUiTest {
            var tick by mutableStateOf(0)
            setTrackedContent {
                tick
                Icon(testVector, "test", modifier = Modifier.testTag("sut"), tint = Color.Red)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tick++ }
            waitForIdle()
            onNodeWithTag("sut").assertStable()
        }

    @Test
    fun icon_tintChange_recomposesTwice() =
        runRecompositionTrackingUiTest {
            var tint by mutableStateOf(Color.Red)
            setTrackedContent {
                Icon(testVector, "test", modifier = Modifier.testTag("sut"), tint = tint)
            }
            waitForIdle()
            resetRecompositionCounts()
            runOnIdle { tint = Color.Blue }
            waitForIdle()
            // budget: 2 — ImageVector Icon forwards to Painter Icon (both named Icon)
            onNodeWithTag("sut").assertRecompositions(exactly = 2)
        }
}
