// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("DEPRECATION")

package io.daio.wild.style

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.materialize
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.daio.wild.style.modifiers.InteractionSourceElement
import io.daio.wild.style.modifiers.StyleRecorder
import io.daio.wild.style.modifiers.recordStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ClickableFastPathTest {
    @Test
    fun currentAndDeprecatedNonNullSourceOverloadsUseOrdinaryModifierChains() {
        val source = MutableInteractionSource()
        val block: StyleScope.() -> Unit = {}

        val modifiers =
            listOf(
                Modifier.clickable(interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.selectable(true, interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.interactable(interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.interactable(selected = true, interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.experimentalClickable(interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.experimentalClickable(interactionSource = source, style = block, onClick = {}),
                Modifier.experimentalSelectable(true, interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.experimentalSelectable(true, interactionSource = source, style = block, onClick = {}),
                Modifier.experimentalInteractable(interactionSource = source, style = StyleDefaults.None, onClick = {}),
                Modifier.experimentalInteractable(interactionSource = source, style = block, onClick = {}),
                Modifier.experimentalInteractable(
                    selected = true,
                    interactionSource = source,
                    style = StyleDefaults.None,
                    onClick = {},
                ),
                Modifier.experimentalInteractable(
                    selected = true,
                    interactionSource = source,
                    style = block,
                    onClick = {},
                ),
            )

        modifiers.forEach { modifier ->
            assertFalse(modifier.hasComposedElement(), "Unexpected composed element in $modifier")
            assertSame(source, modifier.interactionSourceElement()?.interactionSource)
        }
    }

    @Test
    fun receiverStaysFirstAndNullStyleAddsNoStyleObserver() {
        val source = MutableInteractionSource()
        val modifier =
            (Modifier then ReceiverMarker).clickable(
                interactionSource = source,
                style = null,
                onClick = {},
            )

        val elements = modifier.elements()

        assertTrue(elements.size > 1)
        assertSame(ReceiverMarker, elements.first())
        assertFalse(modifier.hasComposedElement())
        assertSame(null, modifier.interactionSourceElement())
    }

    @Test
    fun nullSourceRemembersOneSourceAcrossRecomposition() =
        runComposeUiTest {
            val capturedSources = mutableListOf<Any?>()
            var generation by mutableIntStateOf(0)

            setContent {
                generation
                val materialized =
                    currentComposer.materialize(
                        Modifier.clickable(
                            interactionSource = null,
                            style = StyleDefaults.None,
                            onClick = {},
                        ),
                    )
                SideEffect {
                    capturedSources += materialized.interactionSourceElement()?.interactionSource
                }
                Box(modifier = materialized)
            }

            runOnIdle { generation++ }
            runOnIdle {
                assertEquals(2, capturedSources.size)
                assertNotNull(capturedSources.first())
                assertSame(capturedSources.first(), capturedSources.last())
            }
        }

    @Test
    fun injectedSourceDrivesStyleObservation() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val recorder = StyleRecorder()

            setContent {
                Box(
                    modifier =
                        Modifier
                            .clickable(
                                interactionSource = source,
                                style = StyleDefaults.None,
                                onClick = {},
                            ).recordStyle(recorder),
                )
            }

            runOnIdle {
                assertTrue(source.tryEmit(PressInteraction.Press(Offset.Zero)))
            }
            runOnIdle { assertTrue(recorder.last.pressed) }
        }
}

private data object ReceiverMarker : Modifier.Element

private fun Modifier.hasComposedElement(): Boolean =
    elements().any { element -> element::class.simpleName?.contains("ComposedModifier") == true }

private fun Modifier.interactionSourceElement(): InteractionSourceElement? =
    elements().filterIsInstance<InteractionSourceElement>().firstOrNull()

private fun Modifier.elements(): List<Modifier.Element> =
    buildList {
        foldIn(Unit) { _, element -> add(element) }
    }
