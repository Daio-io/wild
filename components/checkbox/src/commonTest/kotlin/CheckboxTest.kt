// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.checkbox

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

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
}

