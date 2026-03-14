// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ToggleableTest {
    @Test
    fun toggleableTogglesStateOnClick() =
        runComposeUiTest {
            var checked by mutableStateOf(false)

            setContent {
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).performClick()
            assertTrue(checked, "Expected checked to be true after click")
        }

    @Test
    fun toggleableTogglesOffOnSecondClick() =
        runComposeUiTest {
            var checked by mutableStateOf(true)

            setContent {
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).performClick()
            assertEquals(false, checked, "Expected checked to be false after toggling off")
        }

    @Test
    fun toggleableRespectsDisabledState() =
        runComposeUiTest {
            var checked by mutableStateOf(false)

            setContent {
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    enabled = false,
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).assertIsNotEnabled()
        }

    @Test
    fun toggleableIsEnabledByDefault() =
        runComposeUiTest {
            setContent {
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).assertIsEnabled()
        }

    @Test
    fun toggleableAppliesToggleableStateSemantics() =
        runComposeUiTest {
            setContent {
                Toggleable(
                    checked = true,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).assert(
                hasToggleableState(ToggleableState.On),
            )
        }

    @Test
    fun toggleableUncheckedHasOffState() =
        runComposeUiTest {
            setContent {
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("toggle")).assert(
                hasToggleableState(ToggleableState.Off),
            )
        }

    @Test
    fun toggleableAppliesSemanticRole() =
        runComposeUiTest {
            setContent {
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier =
                        Modifier
                            .testTag("toggle")
                            .size(48.dp)
                            .semantics { role = Role.Switch },
                ) {}
            }

            onNode(hasTestTag("toggle")).assert(
                hasRole(Role.Switch),
            )
        }

    @Test
    fun toggleableAppliesCheckboxRole() =
        runComposeUiTest {
            setContent {
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier =
                        Modifier
                            .testTag("toggle")
                            .size(48.dp)
                            .semantics { role = Role.Checkbox },
                ) {}
            }

            onNode(hasTestTag("toggle")).assert(
                hasRole(Role.Checkbox),
            )
        }
}

@OptIn(ExperimentalTestApi::class)
class SelectableTest {
    @Test
    fun selectableCallsOnClickWhenClicked() =
        runComposeUiTest {
            var clicked = false

            setContent {
                Selectable(
                    selected = false,
                    onClick = { clicked = true },
                    modifier = Modifier.testTag("select").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("select")).performClick()
            assertTrue(clicked, "Expected onClick to be called")
        }

    @Test
    fun selectableDoesNotToggle() =
        runComposeUiTest {
            // Selectable should call onClick, not toggle - parent manages selection
            var clickCount = 0

            setContent {
                Selectable(
                    selected = true,
                    onClick = { clickCount++ },
                    modifier = Modifier.testTag("select").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("select")).performClick()
            assertEquals(1, clickCount, "Expected onClick to be called once")
        }

    @Test
    fun selectableAppliesSelectedSemantics() =
        runComposeUiTest {
            setContent {
                Selectable(
                    selected = true,
                    onClick = {},
                    modifier = Modifier.testTag("select").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("select")).assert(
                hasSelectedState(true),
            )
        }

    @Test
    fun selectableUnselectedHasCorrectSemantics() =
        runComposeUiTest {
            setContent {
                Selectable(
                    selected = false,
                    onClick = {},
                    modifier = Modifier.testTag("select").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("select")).assert(
                hasSelectedState(false),
            )
        }

    @Test
    fun selectableAppliesRadioButtonRole() =
        runComposeUiTest {
            setContent {
                Selectable(
                    selected = false,
                    onClick = {},
                    modifier =
                        Modifier
                            .testTag("select")
                            .size(48.dp)
                            .semantics { role = Role.RadioButton },
                ) {}
            }

            onNode(hasTestTag("select")).assert(
                hasRole(Role.RadioButton),
            )
        }

    @Test
    fun selectableRespectsDisabledState() =
        runComposeUiTest {
            setContent {
                Selectable(
                    selected = false,
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.testTag("select").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("select")).assertIsNotEnabled()
        }
}
