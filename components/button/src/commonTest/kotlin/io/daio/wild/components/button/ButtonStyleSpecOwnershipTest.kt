// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.screenshot.dump
import io.daio.wild.screenshot.firstSourceOwnerHasDirectLayoutNode
import io.daio.wild.screenshot.ownedInteractionSources
import io.daio.wild.screenshot.styleScopeParentCount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ButtonStyleSpecOwnershipTest {
    @Test
    fun defaultValueCall_selectsStyleOverload() =
        runComposeUiTest {
            var clicks = 0
            setContent {
                Button(onClick = { clicks++ }, modifier = Modifier.testTag("button").size(48.dp)) {}
            }
            onNodeWithTag("button").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun specOverload_compiles_allSlots() =
        runComposeUiTest {
            val spec = ButtonDefaults.styleSpec { if (focused) scale = 1.1f }
            var clicks = 0
            setContent {
                Button(
                    onClick = { clicks++ },
                    modifier = Modifier.testTag("button").size(48.dp),
                    style = spec,
                ) {}
            }
            onNodeWithTag("button").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun oneSharedSource_oneStyleChain() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val spec = ButtonDefaults.styleSpec()
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Button(
                    onClick = {},
                    modifier = Modifier.testTag("button").size(48.dp),
                    style = spec,
                    interactionSource = source,
                ) {}
            }

            runOnIdle {
                val sources = compositionData.ownedInteractionSources()
                assertEquals(1, sources.size, compositionData.dump())
                assertSame(source, sources.single())
                assertEquals(
                    1,
                    onNodeWithTag("button").fetchSemanticsNode().styleScopeParentCount(),
                    compositionData.dump(),
                )
            }
        }

    @Test
    fun ownedSource_noNullableComposedPath() =
        runComposeUiTest {
            val spec = ButtonDefaults.styleSpec()
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Button(onClick = {}, modifier = Modifier.size(48.dp), style = spec) {}
            }

            runOnIdle {
                assertEquals(1, compositionData.ownedInteractionSources().size)
                assertTrue(compositionData.firstSourceOwnerHasDirectLayoutNode())
            }
        }

    @Test
    fun enabledClickFocusSemantics_unchanged() =
        runComposeUiTest {
            val spec = ButtonDefaults.styleSpec()
            var clicks = 0
            setContent {
                Button(
                    onClick = { clicks++ },
                    modifier = Modifier.testTag("button").size(48.dp),
                    style = spec,
                ) {}
            }
            val node = onNodeWithTag("button").fetchSemanticsNode()
            assertTrue(SemanticsProperties.Disabled !in node.config)
            assertEquals(
                androidx.compose.ui.semantics.Role.Button,
                node.config[SemanticsProperties.Role],
            )
            assertTrue(SemanticsActions.RequestFocus in node.config)
            assertTrue(SemanticsActions.OnClick in node.config)
            onNodeWithTag("button").performSemanticsAction(SemanticsActions.RequestFocus)
            onNodeWithTag("button").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }
}
