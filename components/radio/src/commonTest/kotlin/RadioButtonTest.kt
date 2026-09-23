// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.radio

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class RadioButtonTest {
    @Test
    fun unselectedRadioButtonExposesUnselectedSemanticsAndRole() =
        runComposeUiTest {
            setContent {
                RadioButton(
                    selected = false,
                    onClick = {},
                    modifier = Modifier.testTag("radio").size(48.dp),
                    indicator = {},
                )
            }

            onNode(hasTestTag("radio"))
                .assert(hasSelectedState(false))
                .assert(hasRole(Role.RadioButton))
        }

    @Test
    fun selectedRadioButtonExposesSemanticsAndInvokesCallerCallback() =
        runComposeUiTest {
            val clickCount = mutableStateOf(0)

            setContent {
                RadioButton(
                    selected = true,
                    onClick = { clickCount.value++ },
                    modifier = Modifier.testTag("radio").size(48.dp),
                    indicator = {},
                )
            }

            onNode(hasTestTag("radio"))
                .assert(hasSelectedState(true))
                .assert(hasRole(Role.RadioButton))
                .performClick()

            assertEquals(1, clickCount.value)
        }

    @Test
    fun radioGroupExposesGroupSemanticsAroundCallerChosenLayout() =
        runComposeUiTest {
            setContent {
                RadioGroup(modifier = Modifier.testTag("group")) {
                    Column {
                        RadioButton(
                            selected = true,
                            onClick = {},
                            modifier = Modifier.testTag("radio"),
                            indicator = {},
                        )
                    }
                }
            }

            onNode(hasTestTag("group")).assert(isSelectableGroup())
            onNode(hasTestTag("radio")).assert(hasSelectedState(true))
        }

    @Test
    fun disabledSelectedRadioPreservesSemanticsAndSuppressesCallback() =
        runComposeUiTest {
            var clickCount by mutableStateOf(0)

            setContent {
                RadioButton(
                    selected = true,
                    onClick = { clickCount++ },
                    enabled = false,
                    modifier = Modifier.testTag("radio").size(48.dp),
                    indicator = {},
                )
            }

            onNode(hasTestTag("radio"))
                .assert(hasSelectedState(true))
                .assertIsNotEnabled()
                .performClick()

            assertEquals(0, clickCount)
        }

    @Test
    fun indicatorReceivesSelectedValueAndUpdatesWithCallerState() =
        runComposeUiTest {
            var selected by mutableStateOf(false)

            setContent {
                RadioButton(
                    selected = selected,
                    onClick = { selected = true },
                    modifier = Modifier.testTag("radio").size(48.dp),
                    indicator = { isSelected ->
                        Box(
                            modifier =
                                Modifier
                                    .testTag(if (isSelected) "selected" else "unselected")
                                    .size(4.dp),
                        )
                    },
                )
            }

            onNode(hasTestTag("unselected"), useUnmergedTree = true).assertIsDisplayed()
            onNode(hasTestTag("radio")).performClick()
            onNode(hasTestTag("selected"), useUnmergedTree = true).assertIsDisplayed()
        }

    @Test
    fun callerOwnedSelectionUpdatesAllRadioButtonsInGroup() =
        runComposeUiTest {
            var selected by mutableStateOf("first")

            setContent {
                RadioGroup {
                    Column {
                        listOf("first", "second").forEach { value ->
                            RadioButton(
                                selected = selected == value,
                                onClick = { selected = value },
                                modifier = Modifier.testTag(value).size(48.dp),
                                indicator = {},
                            )
                        }
                    }
                }
            }

            onNode(hasTestTag("first")).assert(hasSelectedState(true))
            onNode(hasTestTag("second")).assert(hasSelectedState(false)).performClick()
            onNode(hasTestTag("first")).assert(hasSelectedState(false))
            onNode(hasTestTag("second")).assert(hasSelectedState(true))
        }
}
