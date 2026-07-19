// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

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
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.content.LocalContentColor
import io.daio.wild.style.StyleDefaults
import kotlinx.coroutines.flow.Flow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ToggleableInteractionSourceOwnershipTest {
    @Test
    fun toggleableKeepsFocusedContentColorAcrossParentRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            var observedContentColor = Color.Unspecified

            setContent {
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.testTag("toggleable-${generation.intValue}").size(48.dp),
                    style = focusedContentStyle(),
                ) { observedContentColor = LocalContentColor.current }
            }

            assertFocusAndColorSurviveRecomposition(
                targetTagPrefix = "toggleable",
                generation = generation,
                observedContentColor = { observedContentColor },
            )
        }

    @Test
    fun selectableKeepsFocusedContentColorAcrossParentRecomposition() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            var observedContentColor = Color.Unspecified

            setContent {
                Selectable(
                    selected = false,
                    onClick = {},
                    modifier = Modifier.testTag("selectable-${generation.intValue}").size(48.dp),
                    style = focusedContentStyle(),
                ) { observedContentColor = LocalContentColor.current }
            }

            assertFocusAndColorSurviveRecomposition(
                targetTagPrefix = "selectable",
                generation = generation,
                observedContentColor = { observedContentColor },
            )
        }

    @Test
    fun toggleableUsesContainerOwnedStableInteractionSource() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            lateinit var compositionData: CompositionData
            val ownedSources = mutableListOf<MutableInteractionSource>()

            setContent {
                generation.intValue
                compositionData = currentComposer.compositionData
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
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
    fun selectableUsesContainerOwnedStableInteractionSource() =
        runComposeUiTest {
            val generation = mutableIntStateOf(0)
            lateinit var compositionData: CompositionData
            val ownedSources = mutableListOf<MutableInteractionSource>()

            setContent {
                generation.intValue
                compositionData = currentComposer.compositionData
                Selectable(
                    selected = false,
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
                assertEquals(1, sources.size)
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
            lateinit var compositionData: CompositionData

            setContent {
                compositionData = currentComposer.compositionData
                Toggleable(
                    checked = false,
                    onCheckedChange = {},
                    modifier = Modifier.size(48.dp),
                    interactionSource = source,
                ) {}
            }

            runOnIdle {
                assertSame(source, compositionData.ownedInteractionSources().single())
            }
        }

    @Test
    fun selectableEmitsFocusIntoExplicitInteractionSource() =
        runComposeUiTest {
            val source = RecordingMutableInteractionSource()

            setContent {
                Selectable(
                    selected = false,
                    onClick = {},
                    modifier = Modifier.testTag("selectable").size(48.dp),
                    interactionSource = source,
                ) {}
            }

            onNodeWithTag("selectable")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            waitForIdle()

            runOnIdle {
                assertTrue(
                    source.emittedInteractions.any {
                        it is androidx.compose.foundation.interaction.FocusInteraction.Focus
                    },
                )
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

private class RecordingMutableInteractionSource : MutableInteractionSource {
    private val delegate = MutableInteractionSource()

    val emittedInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()

    override val interactions: Flow<androidx.compose.foundation.interaction.Interaction> = delegate.interactions

    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {
        emittedInteractions += interaction
        delegate.emit(interaction)
    }

    override fun tryEmit(interaction: androidx.compose.foundation.interaction.Interaction): Boolean {
        emittedInteractions += interaction
        return delegate.tryEmit(interaction)
    }
}

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
