// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SwitchInteractionSourceTest {
    @Test
    fun switchEmitsFocusToSuppliedInteractionSource() =
        runComposeUiTest {
            val source = SwitchRecordingInteractionSource()

            setContent {
                Switch(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("switch").size(48.dp),
                    interactionSource = source,
                ) {}
            }

            onNodeWithTag("switch")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()

            runOnIdle {
                assertTrue(source.emittedInteractions.any { it is FocusInteraction.Focus })
            }
        }
}

private class SwitchRecordingInteractionSource : MutableInteractionSource {
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
