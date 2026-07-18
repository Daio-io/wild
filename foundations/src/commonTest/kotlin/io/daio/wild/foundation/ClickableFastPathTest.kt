// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalWildApi::class)
class ClickableFastPathTest {
    @Test
    fun nonNullInteractionSourcesUseOrdinaryModifierChains() {
        val source = MutableInteractionSource()

        val modifiers =
            listOf(
                Modifier.clickable(interactionSource = source, onClick = {}),
                Modifier.selectable(selected = true, interactionSource = source, onClick = {}),
                Modifier.interactable(interactionSource = source, onClick = {}),
                Modifier.interactable(selected = true, interactionSource = source, onClick = {}),
                Modifier.clickable(interactionSource = null, onClick = {}),
            )

        modifiers.forEach { modifier ->
            assertFalse(modifier.hasComposedElement(), "Unexpected composed element in $modifier")
        }
    }

    @Test
    fun receiverStaysFirstInTheFastPathChain() {
        val source = MutableInteractionSource()
        val modifier =
            (Modifier then ReceiverMarker).clickable(
                interactionSource = source,
                onClick = {},
            )

        val elements = modifier.elements()

        assertTrue(elements.size > 1)
        assertSame(ReceiverMarker, elements.first())
    }

    @Test
    fun nonHardwareClickableRemainsFocusableAndClickable() =
        runComposeUiTest {
            var clicks = 0
            val requester = FocusRequester()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = false),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            onNodeWithTag(TARGET_TAG).assertIsFocused().performClick()
            runOnIdle { assertEquals(1, clicks) }
        }

    @Test
    fun hardwareEnterAndLongClickSemanticsInvokeCallbacks() =
        runComposeUiTest {
            var clicks = 0
            var longClicks = 0
            val requester = FocusRequester()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    onLongClick = { longClicks++ },
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            target.assertIsFocused()
            target.performKeyInput { keyDown(Key.Enter) }
            waitForIdle()
            target.performKeyInput { keyUp(Key.Enter) }
            runOnIdle { assertEquals(1, clicks) }

            target.performSemanticsAction(SemanticsActions.OnLongClick)
            runOnIdle { assertEquals(1, longClicks) }
        }

    @Test
    fun hardwareDoubleEnterInvokesDoubleClick() =
        runComposeUiTest {
            var clicks = 0
            var doubleClicks = 0
            val requester = FocusRequester()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    onDoubleClick = { doubleClicks++ },
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            repeat(2) {
                target.performKeyInput { keyDown(Key.Enter) }
                waitForIdle()
                target.performKeyInput { keyUp(Key.Enter) }
                waitForIdle()
            }

            runOnIdle {
                assertEquals(0, clicks)
                assertEquals(1, doubleClicks)
            }
        }

    @Test
    fun disabledHardwareSelectableRemainsFocusedSelectedAndDoesNotHandleEnter() =
        runComposeUiTest {
            var clicks = 0
            val requester = FocusRequester()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .selectable(
                                    selected = true,
                                    enabled = false,
                                    interactionSource = MutableInteractionSource(),
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            target.assertIsFocused().assertIsSelected().assertIsNotEnabled()
            target.performKeyInput { keyDown(Key.Enter) }
            waitForIdle()
            target.performKeyInput { keyUp(Key.Enter) }
            runOnIdle { assertEquals(0, clicks) }
        }

    @Test
    fun hardwareSemanticsMergeDescendants() =
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    onClick = {},
                                ).testTag(TARGET_TAG),
                    ) {
                        Box(modifier = Modifier.semantics { contentDescription = "Child" })
                    }
                }
            }

            onNodeWithTag(TARGET_TAG).assertContentDescriptionEquals("Child")
        }

    @Test
    fun leavingHardwareModeReleasesAnActivePress() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val interactions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var hardwareInput by mutableStateOf(true)

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides
                        PlatformInteractions(requiresHardwareInput = hardwareInput),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .clickable(interactionSource = source, onClick = {})
                                .testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                    LaunchedEffect(source) {
                        source.interactions.collect { interactions += it }
                    }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            target.performKeyInput { keyDown(Key.Enter) }
            waitForIdle()
            runOnIdle { hardwareInput = false }
            waitForIdle()

            runOnIdle {
                assertTrue(interactions.filterIsInstance<PressInteraction>().last() is PressInteraction.Release)
            }
        }
}

private const val TARGET_TAG = "target"

private data object ReceiverMarker : Modifier.Element

private fun Modifier.hasComposedElement(): Boolean =
    elements().any { element -> element::class.simpleName?.contains("ComposedModifier") == true }

private fun Modifier.elements(): List<Modifier.Element> =
    buildList {
        foldIn(Unit) { _, element -> add(element) }
    }
