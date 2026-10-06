// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.styleSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ToggleableStyleSpecOwnershipTest {
    @Test
    fun defaultValueCall_selectsStyleOverload() =
        runComposeUiTest {
            var checked by mutableStateOf(false)
            setContent {
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }
            onNodeWithTag("toggle").performClick()
            runOnIdle { assertTrue(checked) }
        }

    @Test
    fun selectableDefaultValueCall_selectsStyleOverload() =
        runComposeUiTest {
            var clicks = 0
            setContent {
                Selectable(
                    selected = false,
                    onClick = { clicks++ },
                    modifier = Modifier.testTag("selectable").size(48.dp),
                ) {}
            }
            onNodeWithTag("selectable").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun specOverload_compiles_allSlots() =
        runComposeUiTest {
            val spec = styleSpec(ToggleableDefaults.style()) { if (selected) scale = 1.05f }
            var checked by mutableStateOf(false)
            setContent {
                Toggleable(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    style = spec,
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }
            onNodeWithTag("toggle").performClick()
            runOnIdle { assertTrue(checked) }

            setContent {
                Selectable(
                    selected = true,
                    onClick = {},
                    style = spec,
                    modifier = Modifier.testTag("selectable").size(48.dp),
                ) {}
            }
            onNodeWithTag("selectable").assertIsSelected()

            setContent {
                Toggleable(
                    state = ToggleableState.On,
                    onClick = {},
                    style = spec,
                    modifier = Modifier.testTag("tri").size(48.dp),
                ) {}
            }
            onNodeWithTag("tri").assertIsOn()
        }

    @Test
    fun oneSharedSource_oneStyleChain() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val spec = styleSpec(ToggleableDefaults.style()) { }
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    style = spec,
                    modifier = Modifier.size(48.dp),
                    interactionSource = source,
                ) {}
            }

            runOnIdle {
                val sources = compositionData.ownedInteractionSources()
                assertEquals(1, sources.size, compositionData.dump())
                assertSame(source, sources.single())
            }
        }

    @Test
    fun ownedSource_noNullableComposedPath() =
        runComposeUiTest {
            val spec = styleSpec(ToggleableDefaults.style()) { }
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Toggleable(
                    checked = true,
                    onCheckedChange = {},
                    style = spec,
                    modifier = Modifier.size(48.dp),
                ) {}
            }

            runOnIdle {
                assertEquals(1, compositionData.ownedInteractionSources().size)
                assertTrue(compositionData.firstSourceOwnerHasDirectLayoutNode())
            }
        }

    @Test
    fun selectedCheckedSemantics_unchanged() =
        runComposeUiTest {
            val spec = styleSpec(ToggleableDefaults.style()) { }
            setContent {
                Toggleable(
                    checked = true,
                    onCheckedChange = {},
                    style = spec,
                    modifier = Modifier.testTag("toggle").size(48.dp),
                ) {}
            }
            onNodeWithTag("toggle").assertIsOn()
            runOnIdle {
                val node = onNodeWithTag("toggle").fetchSemanticsNode()
                assertEquals(ToggleableState.On, node.config[SemanticsProperties.ToggleableState])
            }
        }
}
