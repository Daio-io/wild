// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.switch

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SwitchTest {
    @Test
    fun switchExposesSwitchRoleAndCheckedStateAndEmitsInverseOnClick() =
        runComposeUiTest {
            var proposedChecked: Boolean? = null

            setContent {
                Switch(
                    checked = false,
                    onCheckedChange = { proposedChecked = it },
                    modifier = Modifier.testTag("switch").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("switch"))
                .assert(hasRole(Role.Switch))
                .assert(hasToggleableState(ToggleableState.Off))
                .performClick()

            assertEquals(true, proposedChecked)
        }

    @Test
    fun checkedSwitchExposesOnStateAndEmitsUncheckedOnClick() =
        runComposeUiTest {
            var proposedChecked: Boolean? = null

            setContent {
                Switch(
                    checked = true,
                    onCheckedChange = { proposedChecked = it },
                    modifier = Modifier.testTag("switch").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("switch"))
                .assert(hasToggleableState(ToggleableState.On))
                .performClick()

            assertEquals(false, proposedChecked)
        }

    @Test
    fun disabledSwitchRetainsCheckedStateAndSuppressesCallback() =
        runComposeUiTest {
            var clickCount = 0

            setContent {
                Switch(
                    checked = true,
                    onCheckedChange = { clickCount++ },
                    enabled = false,
                    modifier = Modifier.testTag("switch").size(48.dp),
                ) {}
            }

            onNode(hasTestTag("switch"))
                .assertIsNotEnabled()
                .assert(hasToggleableState(ToggleableState.On))
                .performClick()

            assertEquals(0, clickCount)
        }

    @Test
    fun rejectedCallbackLeavesSwitchStateAndSlotControlledByCaller() =
        runComposeUiTest {
            var checked by mutableStateOf(false)
            val proposedValues = mutableListOf<Boolean>()
            var slotChecked = false

            setContent {
                Switch(
                    checked = checked,
                    onCheckedChange = { proposedValues += it },
                    modifier = Modifier.testTag("switch").size(48.dp),
                ) { slotChecked = it }
            }

            onNode(hasTestTag("switch"))
                .performClick()
                .performClick()

            onNode(hasTestTag("switch")).assert(hasToggleableState(ToggleableState.Off))
            runOnIdle {
                assertEquals(listOf(true, true), proposedValues)
                assertEquals(false, slotChecked)
            }
        }

    @Test
    fun switchSlotReflectsExternallyControlledRecomposition() =
        runComposeUiTest {
            var checked by mutableStateOf(false)
            var slotChecked = false

            setContent {
                Switch(
                    checked = checked,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("switch").size(48.dp),
                ) { slotChecked = it }
            }

            runOnIdle { assertEquals(false, slotChecked) }
            runOnIdle { checked = true }

            onNode(hasTestTag("switch")).assert(hasToggleableState(ToggleableState.On))
            runOnIdle { assertEquals(true, slotChecked) }
        }
}

private fun hasRole(role: Role): SemanticsMatcher = SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

private fun hasToggleableState(state: ToggleableState): SemanticsMatcher =
    SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, state)
