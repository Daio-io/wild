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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ContainerInteractionSourceOwnershipTest {
    @Test
    fun implicitInteractionSourceKeepsFocusedContentColorAcrossParentRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            var observedContentColor = Color.Unspecified

            setContent {
                Container(
                    onClick = {},
                    modifier = Modifier.testTag("container-${generation.intValue}").size(48.dp),
                    style = focusedContentStyle(),
                ) { observedContentColor = LocalContentColor.current }
            }

            runOnIdle { assertEquals(UnfocusedContentColor, observedContentColor) }
            onNodeWithTag("container-0")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()
            runOnIdle { assertEquals(FocusedContentColor, observedContentColor) }

            runOnIdle { generation.intValue++ }

            onNodeWithTag("container-1").assertIsFocused()
            runOnIdle { assertEquals(FocusedContentColor, observedContentColor) }
        }

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
                ownedSources += compositionData.ownedInteractionSources().single()
                generation.intValue++
            }
            runOnIdle {
                val sources = compositionData.ownedInteractionSources()
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
                assertSame(source, compositionData.ownedInteractionSources().single())
            }
        }
}

private val UnfocusedContentColor = Color.Red
private val FocusedContentColor = Color.Green

private fun focusedContentStyle() =
    StyleDefaults.style(
        colors =
            StyleDefaults.colors(
                contentColor = UnfocusedContentColor,
                focusedContentColor = FocusedContentColor,
            ),
    )

private fun CompositionData.ownedInteractionSources(): List<MutableInteractionSource> =
    firstSourceOwnerGroup()
        ?.allInteractionSources()
        .orEmpty()

private fun CompositionGroup.allInteractionSources(): List<MutableInteractionSource> =
    mutableListOf<MutableInteractionSource>().also(::collectInteractionSources)

private fun CompositionGroup.collectInteractionSources(sources: MutableList<MutableInteractionSource>) {
    data.filterIsInstance<MutableInteractionSource>().forEach { source ->
        if (sources.none { it === source }) {
            sources += source
        }
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
