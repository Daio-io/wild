// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

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
import kotlin.test.assertFalse
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class ListItemInteractionSourceOwnershipTest {
    @Test
    fun contentOnlyListItemKeepsFocusedContentColorAcrossParentRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            var observedContentColor = Color.Unspecified

            setContent {
                ListItem(
                    onClick = {},
                    modifier =
                        Modifier
                            .testTag("content-only-list-item-${generation.intValue}")
                            .size(48.dp),
                    style = focusedContentStyle(),
                ) { observedContentColor = LocalContentColor.current }
            }

            assertFocusAndColorSurviveRecomposition(
                targetTagPrefix = "content-only-list-item",
                generation = generation,
                observedContentColor = { observedContentColor },
            )
        }

    @Test
    fun slottedListItemKeepsFocusedContentColorAcrossParentRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            var observedContentColor = Color.Unspecified

            setContent {
                ListItem(
                    onClick = {},
                    leadingContent = {},
                    trailingContent = {},
                    modifier = Modifier.testTag("slotted-list-item-${generation.intValue}").size(48.dp),
                    style = focusedContentStyle(),
                ) { observedContentColor = LocalContentColor.current }
            }

            assertFocusAndColorSurviveRecomposition(
                targetTagPrefix = "slotted-list-item",
                generation = generation,
                observedContentColor = { observedContentColor },
            )
        }

    @Test
    fun contentOnlyListItemOwnsStableImplicitInteractionSource() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            lateinit var compositionData: CompositionData
            val ownedSources = mutableListOf<MutableInteractionSource>()

            setContent {
                generation.intValue
                compositionData = currentComposer.compositionData
                ListItem(
                    onClick = {},
                    modifier = Modifier.size(48.dp),
                ) {}
            }

            runOnIdle {
                ownedSources += compositionData.directlyOwnedInteractionSources().single()
                generation.intValue++
            }
            runOnIdle {
                val sources = compositionData.directlyOwnedInteractionSources()
                assertEquals(1, sources.size, compositionData.dump())
                assertFalse(compositionData.firstSourceOwnerHasDirectLayoutNode())
                ownedSources += sources.single()
                assertEquals(2, ownedSources.size)
                assertSame(ownedSources.first(), ownedSources.last())
            }
        }

    @Test
    fun slottedListItemOwnsStableImplicitInteractionSource() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            lateinit var compositionData: CompositionData
            val ownedSources = mutableListOf<MutableInteractionSource>()

            setContent {
                generation.intValue
                compositionData = currentComposer.compositionData
                ListItem(
                    onClick = {},
                    leadingContent = {},
                    trailingContent = {},
                    modifier = Modifier.size(48.dp),
                ) {}
            }

            runOnIdle {
                ownedSources += compositionData.directlyOwnedInteractionSources().single()
                generation.intValue++
            }
            runOnIdle {
                val sources = compositionData.directlyOwnedInteractionSources()
                assertEquals(1, sources.size)
                assertFalse(compositionData.firstSourceOwnerHasDirectLayoutNode())
                ownedSources += sources.single()
                assertEquals(2, ownedSources.size)
                assertSame(ownedSources.first(), ownedSources.last())
            }
        }

    @Test
    fun contentOnlyListItemPreservesExplicitInteractionSource() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            var clickCount = 0
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                ListItem(
                    onClick = { clickCount++ },
                    modifier = Modifier.testTag("content-only-list-item").size(48.dp),
                    interactionSource = source,
                ) {}
            }

            onNodeWithTag("content-only-list-item").performClick()
            runOnIdle {
                assertEquals(1, clickCount)
                assertFalse(compositionData.firstSourceOwnerHasDirectLayoutNode())
                assertSame(source, compositionData.directlyOwnedInteractionSources().single())
            }
        }

    @Test
    fun slottedListItemPreservesExplicitInteractionSource() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            var clickCount = 0
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                ListItem(
                    onClick = { clickCount++ },
                    leadingContent = {},
                    trailingContent = {},
                    modifier = Modifier.testTag("slotted-list-item").size(48.dp),
                    interactionSource = source,
                ) {}
            }

            onNodeWithTag("slotted-list-item").performClick()
            runOnIdle {
                assertEquals(1, clickCount)
                assertFalse(compositionData.firstSourceOwnerHasDirectLayoutNode())
                assertSame(source, compositionData.directlyOwnedInteractionSources().single())
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

@OptIn(ExperimentalTestApi::class)
private fun androidx.compose.ui.test.ComposeUiTest.assertFocusAndColorSurviveRecomposition(
    targetTagPrefix: String,
    generation: androidx.compose.runtime.MutableIntState,
    observedContentColor: () -> Color,
) {
    runOnIdle { assertEquals(UnfocusedContentColor, observedContentColor()) }
    onNodeWithTag("$targetTagPrefix-0")
        .performSemanticsAction(SemanticsActions.RequestFocus)
        .assertIsFocused()
    waitForIdle()
    runOnIdle { assertEquals(FocusedContentColor, observedContentColor()) }

    runOnIdle { generation.intValue++ }

    onNodeWithTag("$targetTagPrefix-1").assertIsFocused()
    runOnIdle { assertEquals(FocusedContentColor, observedContentColor()) }
}

private fun CompositionData.directlyOwnedInteractionSources(): List<MutableInteractionSource> =
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
