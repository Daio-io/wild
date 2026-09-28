// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SliderTest {
    @Test
    fun sliderExposesProgressSemanticsAndSlotValues() =
        runComposeUiTest {
            var slotValue = -1f
            setContent {
                Slider(
                    value = 0.25f,
                    onValueChange = {},
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = {
                        slotValue = value
                        Box(Modifier.testTag("thumb").size(20.dp))
                    },
                    track = { Box(Modifier.testTag("track").size(200.dp, 4.dp)) },
                )
            }

            assertEquals(0.25f, slotValue)
            assertEquals(
                ProgressBarRangeInfo(0.25f, 0f..1f),
                onNode(hasTestTag("slider")).fetchSemanticsNode().config[
                    SemanticsProperties.ProgressBarRangeInfo,
                ],
            )
        }

    @Test
    fun sliderTapSnapsAndRemainsExternallyControlled() =
        runComposeUiTest {
            var proposed = -1f
            setContent {
                Slider(
                    value = 0f,
                    onValueChange = { proposed = it },
                    modifier = Modifier.testTag("slider").width(200.dp),
                    steps = 3,
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("slider")).performTouchInput { click(Offset(100f, 24f)) }
            assertEquals(0.5f, proposed)
        }

    @Test
    fun sliderReportsConfiguredDiscreteStepsInSemantics() =
        runComposeUiTest {
            setContent {
                Slider(
                    value = 0.25f,
                    onValueChange = {},
                    steps = 3,
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            assertEquals(
                ProgressBarRangeInfo(0.25f, 0f..1f, 3),
                onNode(hasTestTag("slider")).fetchSemanticsNode().config[
                    SemanticsProperties.ProgressBarRangeInfo,
                ],
            )
        }

    @Test
    fun disabledSliderSuppressesInput() =
        runComposeUiTest {
            var changes = 0
            setContent {
                Slider(
                    value = 0f,
                    onValueChange = { changes++ },
                    enabled = false,
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("slider")).assertIsNotEnabled()
            assertEquals(0, changes)
        }

    @Test
    fun rangeSliderExposesBothThumbValues() =
        runComposeUiTest {
            var startValue = -1f
            var endValue = -1f
            setContent {
                RangeSlider(
                    value = 0.25f..0.75f,
                    onValueChange = {},
                    modifier = Modifier.testTag("range-slider"),
                    startThumb = { startValue = value.start },
                    endThumb = { endValue = value.endInclusive },
                    track = {},
                )
            }

            assertEquals(0.25f, startValue)
            assertEquals(0.75f, endValue)
        }

    @Test
    fun rangeSliderTapUpdatesNearestThumbWithoutCrossing() =
        runComposeUiTest {
            var proposed = 0f..1f
            setContent {
                RangeSlider(
                    value = 0.25f..0.75f,
                    onValueChange = { proposed = it },
                    modifier = Modifier.testTag("range-slider").width(200.dp),
                    startThumb = { Box(Modifier.size(20.dp)) },
                    endThumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("range-slider")).performTouchInput { click(Offset(190f, 24f)) }
            assertEquals(0.25f..0.95f, proposed)
        }

    @Test
    fun collapsedRangeCanExpandTowardLowerValues() =
        runComposeUiTest {
            var proposed = 0.5f..0.5f
            setContent {
                RangeSlider(
                    value = 0.5f..0.5f,
                    onValueChange = { proposed = it },
                    modifier = Modifier.testTag("range-slider").width(200.dp),
                    startThumb = { Box(Modifier.size(20.dp)) },
                    endThumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("range-slider")).performTouchInput { click(Offset(20f, 24f)) }
            assertEquals(0.1f..0.5f, proposed)
        }
}
