// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.runtime.tooling.CompositionGroup
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ContainerInteractionSourceOwnershipTest {
    @Test
    fun implicitInteractionSourceIsOwnedByContainerAndStableAcrossRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            lateinit var compositionData: CompositionData
            val ownedSources = mutableListOf<MutableInteractionSource>()

            setContent {
                generation.intValue
                compositionData = currentComposer.compositionData
                Container(
                    onClick = {},
                    modifier = Modifier.size(48.dp),
                ) {}
            }

            runOnIdle {
                ownedSources += compositionData.firstDirectlyOwnedInteractionSources().single()
                generation.intValue++
            }
            runOnIdle {
                val sources = compositionData.firstDirectlyOwnedInteractionSources()
                assertEquals(1, sources.size, compositionData.dump())
                assertTrue(compositionData.firstSourceOwnerHasDirectLayoutNode())
                ownedSources += sources.single()
                assertEquals(2, ownedSources.size)
                assertSame(ownedSources.first(), ownedSources.last())
            }
        }

    @Test
    fun explicitInteractionSourceIsPreserved() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            var clickCount = 0
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Container(
                    onClick = { clickCount++ },
                    modifier = Modifier.testTag("container").size(48.dp),
                    interactionSource = source,
                ) {}
            }

            onNodeWithTag("container").performClick()
            runOnIdle {
                assertEquals(1, clickCount)
                assertSame(source, compositionData.firstDirectlyOwnedInteractionSources().single())
            }
        }
}

private fun CompositionData.firstDirectlyOwnedInteractionSources(): List<MutableInteractionSource> =
    firstSourceOwnerGroup()
        ?.compositionGroups
        ?.flatMap { group -> group.data.filterIsInstance<MutableInteractionSource>() }
        ?.distinct()
        .orEmpty()

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
