// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.slider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
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
    fun sliderKeyboardArrowsAdjustContinuousValueAndFinishOnKeyUp() =
        runComposeUiTest {
            var proposed = -1f
            var finished = 0
            setContent {
                Slider(
                    value = 0.5f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            val slider = onNode(hasTestTag("slider"))
            slider.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            slider.performKeyInput {
                keyDown(Key.DirectionRight)
                keyUp(Key.DirectionRight)
            }
            assertEquals(0.51f, proposed)
            assertEquals(1, finished)

            slider.performKeyInput {
                keyDown(Key.DirectionLeft)
                keyUp(Key.DirectionLeft)
            }
            assertEquals(0.49f, proposed)
            assertEquals(2, finished)

            slider.performKeyInput {
                keyDown(Key.DirectionUp)
                keyUp(Key.DirectionUp)
            }
            assertEquals(0.51f, proposed)

            slider.performKeyInput {
                keyDown(Key.DirectionDown)
                keyUp(Key.DirectionDown)
            }
            assertEquals(0.49f, proposed)
            assertEquals(4, finished)
        }

    @Test
    fun sliderKeyboardDiscreteStepsHomeEndAndPageKeys() =
        runComposeUiTest {
            var proposed = -1f
            var finished = 0
            setContent {
                Slider(
                    value = 0.5f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    steps = 3,
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            val slider = onNode(hasTestTag("slider"))
            slider.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()

            slider.performKeyInput {
                keyDown(Key.DirectionRight)
                keyUp(Key.DirectionRight)
            }
            assertEquals(0.75f, proposed)

            slider.performKeyInput {
                keyDown(Key.MoveHome)
                keyUp(Key.MoveHome)
            }
            assertEquals(0f, proposed)

            slider.performKeyInput {
                keyDown(Key.MoveEnd)
                keyUp(Key.MoveEnd)
            }
            assertEquals(1f, proposed)

            slider.performKeyInput {
                keyDown(Key.PageDown)
                keyUp(Key.PageDown)
            }
            assertEquals(0f, proposed)
            assertEquals(4, finished)
        }

    @Test
    fun sliderKeyboardRtlReversesHorizontalArrows() =
        runComposeUiTest {
            var proposed = -1f
            setContent {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Slider(
                        value = 0.5f,
                        onValueChange = { proposed = it },
                        modifier = Modifier.testTag("slider").width(200.dp),
                        thumb = { Box(Modifier.size(20.dp)) },
                        track = { Box(Modifier.size(200.dp, 4.dp)) },
                    )
                }
            }

            val slider = onNode(hasTestTag("slider"))
            slider.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            slider.performKeyInput {
                keyDown(Key.DirectionRight)
                keyUp(Key.DirectionRight)
            }
            assertEquals(0.49f, proposed)

            slider.performKeyInput {
                keyDown(Key.DirectionLeft)
                keyUp(Key.DirectionLeft)
            }
            assertEquals(0.51f, proposed)
        }

    @Test
    fun sliderSetProgressSnapsAndInvokesFinished() =
        runComposeUiTest {
            var proposed = -1f
            var finished = 0
            setContent {
                Slider(
                    value = 0.25f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    steps = 3,
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("slider")).performSemanticsAction(SemanticsActions.SetProgress) {
                it(0.6f)
            }
            assertEquals(0.5f, proposed)
            assertEquals(1, finished)
        }

    @Test
    fun sliderDragUpdatesValueAndInvokesFinished() =
        runComposeUiTest {
            var proposed = -1f
            var finished = 0
            setContent {
                Slider(
                    value = 0f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.testTag("slider").width(200.dp),
                    thumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasTestTag("slider")).performTouchInput {
                down(Offset(20f, 24f))
                moveTo(Offset(190f, 24f))
                up()
            }
            assertEquals(1f, proposed)
            assertEquals(1, finished)
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

    @Test
    fun rangeSliderKeyboardMovesFocusedThumbWithoutCrossing() =
        runComposeUiTest {
            var proposed = 0.25f..0.75f
            var finished = 0
            setContent {
                RangeSlider(
                    value = 0.25f..0.75f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.testTag("range-slider").width(200.dp),
                    startThumb = { Box(Modifier.size(20.dp)) },
                    endThumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            val startThumb = onNode(hasProgressCurrent(0.25f))
            startThumb.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            startThumb.performKeyInput {
                keyDown(Key.DirectionRight)
                keyUp(Key.DirectionRight)
            }
            assertEquals(0.26f..0.75f, proposed)
            assertEquals(1, finished)

            startThumb.performKeyInput {
                keyDown(Key.MoveEnd)
                keyUp(Key.MoveEnd)
            }
            assertEquals(0.75f..0.75f, proposed)

            val endThumb = onNode(hasProgressCurrent(0.75f))
            endThumb.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            endThumb.performKeyInput {
                keyDown(Key.DirectionLeft)
                keyUp(Key.DirectionLeft)
            }
            assertEquals(0.25f..0.74f, proposed)

            endThumb.performKeyInput {
                keyDown(Key.MoveHome)
                keyUp(Key.MoveHome)
            }
            assertEquals(0.25f..0.25f, proposed)
        }

    @Test
    fun rangeSliderKeyboardPageKeysAndDiscreteSteps() =
        runComposeUiTest {
            var proposed = 0.25f..0.75f
            setContent {
                RangeSlider(
                    value = 0.25f..0.75f,
                    onValueChange = { proposed = it },
                    steps = 3,
                    modifier = Modifier.testTag("range-slider").width(200.dp),
                    startThumb = { Box(Modifier.size(20.dp)) },
                    endThumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            val endThumb =
                onNode(
                    SemanticsMatcher.expectValue(
                        SemanticsProperties.ProgressBarRangeInfo,
                        ProgressBarRangeInfo(0.75f, 0f..1f, 3),
                    ),
                )
            endThumb.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            endThumb.performKeyInput {
                keyDown(Key.DirectionRight)
                keyUp(Key.DirectionRight)
            }
            assertEquals(0.25f..1f, proposed)

            val startThumb =
                onNode(
                    SemanticsMatcher.expectValue(
                        SemanticsProperties.ProgressBarRangeInfo,
                        ProgressBarRangeInfo(0.25f, 0f..1f, 3),
                    ),
                )
            startThumb.performSemanticsAction(SemanticsActions.RequestFocus).assertIsFocused()
            startThumb.performKeyInput {
                keyDown(Key.PageUp)
                keyUp(Key.PageUp)
            }
            assertEquals(0.75f..0.75f, proposed)
        }

    @Test
    fun rangeSliderSetProgressOnThumbUpdatesRange() =
        runComposeUiTest {
            var proposed = 0.25f..0.75f
            var finished = 0
            setContent {
                RangeSlider(
                    value = 0.25f..0.75f,
                    onValueChange = { proposed = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.testTag("range-slider").width(200.dp),
                    startThumb = { Box(Modifier.size(20.dp)) },
                    endThumb = { Box(Modifier.size(20.dp)) },
                    track = { Box(Modifier.size(200.dp, 4.dp)) },
                )
            }

            onNode(hasProgressCurrent(0.75f)).performSemanticsAction(SemanticsActions.SetProgress) {
                it(0.9f)
            }
            assertEquals(0.25f..0.9f, proposed)
            assertEquals(1, finished)
        }
}

private fun hasProgressCurrent(current: Float): SemanticsMatcher =
    SemanticsMatcher.expectValue(
        SemanticsProperties.ProgressBarRangeInfo,
        ProgressBarRangeInfo(current, 0f..1f),
    )
