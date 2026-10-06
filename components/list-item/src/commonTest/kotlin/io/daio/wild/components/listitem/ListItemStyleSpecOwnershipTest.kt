// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.runtime.tooling.CompositionGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
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
class ListItemStyleSpecOwnershipTest {
    @Test
    fun defaultValueCall_selectsStyleOverload() =
        runComposeUiTest {
            var clicks = 0
            setContent {
                ListItem(onClick = { clicks++ }, modifier = Modifier.testTag("item").size(48.dp)) {
                    BasicText("item")
                }
            }
            onNodeWithTag("item").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun specOverload_compiles_allSlots() =
        runComposeUiTest {
            val spec = styleSpec(ListItemDefaults.style()) { if (focused) scale = 1.05f }
            var clicks = 0
            setContent {
                ListItem(
                    onClick = { clicks++ },
                    leadingContent = { BasicText("L") },
                    trailingContent = { BasicText("T") },
                    style = spec,
                    modifier = Modifier.testTag("item").size(48.dp),
                ) {
                    BasicText("content")
                }
            }
            onNodeWithTag("item").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun oneSharedSource_oneStyleChain() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val spec = styleSpec(ListItemDefaults.style()) { }
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                ListItem(
                    onClick = {},
                    style = spec,
                    modifier = Modifier.size(48.dp),
                    interactionSource = source,
                ) {
                    BasicText("item")
                }
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
            val spec = styleSpec(ListItemDefaults.style()) { }
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                ListItem(onClick = {}, style = spec, modifier = Modifier.size(48.dp)) {
                    BasicText("item")
                }
            }

            runOnIdle {
                assertEquals(1, compositionData.ownedInteractionSources().size)
                assertTrue(compositionData.firstSourceOwnerHasDirectLayoutNode())
            }
        }

    @Test
    fun selectedCheckedSemantics_unchanged() =
        runComposeUiTest {
            val spec = styleSpec(ListItemDefaults.style()) { }
            var clicks = 0
            setContent {
                ListItem(
                    onClick = { clicks++ },
                    selected = true,
                    style = spec,
                    modifier = Modifier.testTag("item").size(48.dp),
                ) {
                    BasicText("selected")
                }
            }
            onNodeWithTag("item").performClick()
            runOnIdle { assertEquals(1, clicks) }
        }
}

private fun CompositionData.ownedInteractionSources(): List<MutableInteractionSource> =
    firstSourceOwnerGroup()?.allInteractionSources().orEmpty()

private fun CompositionGroup.allInteractionSources(): List<MutableInteractionSource> =
    mutableListOf<MutableInteractionSource>().also(::collectInteractionSources)

private fun CompositionGroup.collectInteractionSources(sources: MutableList<MutableInteractionSource>) {
    data.filterIsInstance<MutableInteractionSource>().forEach { source ->
        if (sources.none { it === source }) sources += source
    }
    compositionGroups.forEach { group -> group.collectInteractionSources(sources) }
}

private fun CompositionData.firstSourceOwnerGroup(): CompositionGroup? =
    compositionGroups.firstNotNullOfOrNull { group ->
        group.takeIf {
            it.compositionGroups.any { child -> child.data.any { value -> value is MutableInteractionSource } }
        } ?: group.firstSourceOwnerGroup()
    }

private fun CompositionData.firstSourceOwnerHasDirectLayoutNode(): Boolean =
    firstSourceOwnerGroup()
        ?.compositionGroups
        ?.any { group -> group.data.any { value -> value?.let { it::class.simpleName } == "LayoutNode" } }
        ?: false

private fun CompositionData.dump(depth: Int = 0): String =
    compositionGroups.joinToString(separator = "\n") { group ->
        "${"  ".repeat(depth)}${group.data.map { value -> value?.let { it::class.simpleName } }}\n${group.dump(depth + 1)}"
    }
