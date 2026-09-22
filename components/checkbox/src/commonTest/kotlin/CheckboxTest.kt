// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.checkbox

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CheckboxTest {
    @Test
    fun uncheckedCheckboxHasCheckboxRoleAndOffStateAndEmitsCheckedOnClick() =
        runComposeUiTest {
            var checked: Boolean? = null

            setContent {
                Checkbox(
                    checked = false,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { }
            }

            onNode(hasTestTag("checkbox")).assert(hasRole(Role.Checkbox))
            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Off))

            onNode(hasTestTag("checkbox")).performClick()

            assertEquals(true, checked)
        }

    @Test
    fun checkedCheckboxEmitsUncheckedOnClick() =
        runComposeUiTest {
            var checked: Boolean? = null

            setContent {
                Checkbox(
                    checked = true,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { }
            }

            onNode(hasTestTag("checkbox")).performClick()

            assertEquals(false, checked)
        }

    @Test
    fun indeterminateTriStateCheckboxExposesStateAndRoleAndDoesNotAutoCycle() =
        runComposeUiTest {
            var clickCount = 0
            var indicatorState: ToggleableState? = null

            setContent {
                TriStateCheckbox(
                    state = ToggleableState.Indeterminate,
                    onClick = { clickCount++ },
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { state -> indicatorState = state }
            }

            onNode(hasTestTag("checkbox")).assert(hasRole(Role.Checkbox))
            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Indeterminate))
            runOnIdle { assertEquals(ToggleableState.Indeterminate, indicatorState) }

            onNode(hasTestTag("checkbox")).performClick()

            assertEquals(1, clickCount)
            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Indeterminate))
        }

    @Test
    fun disabledCheckboxDoesNotInvokeCallback() =
        runComposeUiTest {
            var clickCount = 0

            setContent {
                Checkbox(
                    checked = true,
                    onCheckedChange = { clickCount++ },
                    enabled = false,
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { }
            }

            onNode(hasTestTag("checkbox")).assertIsNotEnabled()
            onNode(hasTestTag("checkbox")).performClick()

            assertEquals(0, clickCount)
        }

    @Test
    fun disabledTriStateCheckboxDoesNotInvokeCallback() =
        runComposeUiTest {
            var clickCount = 0

            setContent {
                TriStateCheckbox(
                    state = ToggleableState.Indeterminate,
                    onClick = { clickCount++ },
                    enabled = false,
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { }
            }

            onNode(hasTestTag("checkbox")).assertIsNotEnabled()
            onNode(hasTestTag("checkbox")).performClick()

            assertEquals(0, clickCount)
        }

    @Test
    fun checkboxReflectsExternallyControlledStateInSemanticsAndIndicator() =
        runComposeUiTest {
            var checked by mutableStateOf(false)
            var indicatorChecked = false

            setContent {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { indicatorChecked = it }
            }

            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Off))
            runOnIdle { assertEquals(false, indicatorChecked) }

            runOnIdle { checked = true }

            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.On))
            runOnIdle { assertEquals(true, indicatorChecked) }
        }

    @Test
    fun triStateCheckboxReflectsAllExternallyControlledStates() =
        runComposeUiTest {
            var state by mutableStateOf(ToggleableState.Off)

            setContent {
                TriStateCheckbox(
                    state = state,
                    onClick = {},
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                ) { }
            }

            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Off))
            runOnIdle { state = ToggleableState.On }
            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.On))
            runOnIdle { state = ToggleableState.Indeterminate }
            onNode(hasTestTag("checkbox")).assert(hasToggleableState(ToggleableState.Indeterminate))
        }

    @Test
    fun checkboxEmitsFocusToSuppliedInteractionSource() =
        runComposeUiTest {
            val source = RecordingMutableInteractionSource()

            setContent {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("checkbox").size(48.dp),
                    interactionSource = source,
                ) { }
            }

            onNode(hasTestTag("checkbox"))
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()

            runOnIdle {
                assertTrue(source.emittedInteractions.any { it is FocusInteraction.Focus })
            }
        }
}

private class RecordingMutableInteractionSource : MutableInteractionSource {
    private val delegate = MutableInteractionSource()

    val emittedInteractions = mutableListOf<Interaction>()

    override val interactions: Flow<Interaction> = delegate.interactions

    override suspend fun emit(interaction: Interaction) {
        emittedInteractions += interaction
        delegate.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        emittedInteractions += interaction
        return delegate.tryEmit(interaction)
    }
}
