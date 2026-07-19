// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

// Desktop-only: runComposeUiTest requires a platform host and Android local unit tests do not provide one.

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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
    fun callerFocusPropertiesDisableBothHardwareFocusTargets() =
        runComposeUiTest {
            val requester = FocusRequester()
            val source = MutableInteractionSource()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .focusProperties { canFocus = false }
                                .clickable(
                                    interactionSource = source,
                                    onClick = {},
                                ).testTag(TARGET_TAG),
                    )
                }
            }

            runOnIdle { assertFalse(requester.requestFocus()) }
            onNodeWithTag(TARGET_TAG).assertIsNotFocused()
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
    fun disabledNonHardwareClickableHasNoFocusSemantics() =
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = false),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .clickable(
                                    enabled = false,
                                    interactionSource = MutableInteractionSource(),
                                    onClick = {},
                                ).testTag(TARGET_TAG),
                    )
                }
            }

            val semantics = onNodeWithTag(TARGET_TAG).assertIsNotEnabled().fetchSemanticsNode().config

            assertFalse(semantics.contains(SemanticsProperties.Focused))
            assertFalse(semantics.contains(SemanticsActions.RequestFocus))
        }

    @Test
    fun focusedLazyItemRemainsPinnedWhenScrolledOutOfView() =
        runComposeUiTest {
            var scrollToItem by mutableStateOf(0)

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    val state = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier.size(width = 100.dp, height = 30.dp),
                        state = state,
                    ) {
                        items(30) { index ->
                            Box(
                                modifier =
                                    Modifier
                                        .size(width = 100.dp, height = 10.dp)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            onClick = {},
                                        ).testTag("item-$index"),
                            )
                        }
                    }
                    LaunchedEffect(scrollToItem) {
                        if (scrollToItem > 0) state.scrollToItem(scrollToItem)
                    }
                }
            }

            onNodeWithTag("item-0")
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()
            runOnIdle { scrollToItem = 20 }
            waitForIdle()

            onNodeWithTag("item-0").assertIsFocused()
        }

    @Test
    fun replacingSourceWhileFocusedBalancesOnlyTheOldSource() =
        runComposeUiTest {
            val firstSource = MutableInteractionSource()
            val secondSource = MutableInteractionSource()
            val firstInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val secondInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var source by mutableStateOf(firstSource)

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
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
                    LaunchedEffect(firstSource) {
                        firstSource.interactions.collect { firstInteractions += it }
                    }
                    LaunchedEffect(secondSource) {
                        secondSource.interactions.collect { secondInteractions += it }
                    }
                }
            }

            waitUntil { firstInteractions.any { it is FocusInteraction.Focus } }
            runOnIdle { source = secondSource }
            waitForIdle()

            runOnIdle {
                val focus = firstInteractions.filterIsInstance<FocusInteraction.Focus>().single()
                val unfocus = firstInteractions.filterIsInstance<FocusInteraction.Unfocus>().single()
                assertSame(focus, unfocus.focus)
                assertTrue(secondInteractions.none { it is FocusInteraction })
            }
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
    fun replacingSourceDuringHardwarePressCancelsTheOldSourceWithoutClicking() =
        runComposeUiTest {
            val firstSource = MutableInteractionSource()
            val secondSource = MutableInteractionSource()
            val firstInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val secondInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var clicks = 0
            var source by mutableStateOf(firstSource)

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
                                    interactionSource = source,
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                    LaunchedEffect(firstSource) {
                        firstSource.interactions.collect { firstInteractions += it }
                    }
                    LaunchedEffect(secondSource) {
                        secondSource.interactions.collect { secondInteractions += it }
                    }
                }
            }

            val target = onNodeWithTag(TARGET_TAG).assertIsFocused()
            target.performKeyInput { keyDown(Key.Enter) }
            waitUntil { firstInteractions.any { it is PressInteraction.Press } }

            runOnIdle { source = secondSource }
            waitForIdle()

            runOnIdle {
                val press = firstInteractions.filterIsInstance<PressInteraction.Press>().single()
                val cancel = firstInteractions.filterIsInstance<PressInteraction.Cancel>().single()
                assertSame(press, cancel.press)
            }

            target.performKeyInput { keyUp(Key.Enter) }
            waitForIdle()

            runOnIdle {
                assertEquals(0, clicks)
                assertTrue(secondInteractions.none { it is PressInteraction })
            }
        }

    @Test
    fun disablingDuringHardwarePressCancelsThePressWithoutClicking() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val interactions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var clicks = 0
            var enabled by mutableStateOf(true)

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
                                    enabled = enabled,
                                    interactionSource = source,
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                    LaunchedEffect(source) {
                        source.interactions.collect { interactions += it }
                    }
                }
            }

            val target = onNodeWithTag(TARGET_TAG).assertIsFocused()
            target.performKeyInput { keyDown(Key.Enter) }
            waitUntil { interactions.any { it is PressInteraction.Press } }

            runOnIdle { enabled = false }
            waitForIdle()

            runOnIdle {
                val press = interactions.filterIsInstance<PressInteraction.Press>().single()
                val cancel = interactions.filterIsInstance<PressInteraction.Cancel>().single()
                assertSame(press, cancel.press)
            }

            target.performKeyInput { keyUp(Key.Enter) }
            waitForIdle()

            runOnIdle { assertEquals(0, clicks) }
        }

    @Test
    fun removingNodeDuringHardwarePressCancelsTheCapturedSourceWithoutClicking() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val interactions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var clicks = 0
            var showTarget by mutableStateOf(true)

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides PlatformInteractions(requiresHardwareInput = true),
                ) {
                    if (showTarget) {
                        Box(
                            modifier =
                                Modifier
                                    .size(10.dp)
                                    .focusRequester(requester)
                                    .clickable(
                                        interactionSource = source,
                                        onClick = { clicks++ },
                                    ).testTag(TARGET_TAG),
                        )
                        LaunchedEffect(requester) { requester.requestFocus() }
                    }
                    LaunchedEffect(source) {
                        source.interactions.collect { interactions += it }
                    }
                }
            }

            val target = onNodeWithTag(TARGET_TAG).assertIsFocused()
            target.performKeyInput { keyDown(Key.Enter) }
            waitUntil { interactions.any { it is PressInteraction.Press } }

            runOnIdle { showTarget = false }
            waitForIdle()

            runOnIdle {
                val press = interactions.filterIsInstance<PressInteraction.Press>().single()
                val cancel = interactions.filterIsInstance<PressInteraction.Cancel>().single()
                assertSame(press, cancel.press)
                assertEquals(0, clicks)
            }
        }

    @Test
    fun longClickDisablingThenReenablingDoesNotCorruptTheNextHardwarePress() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val interactions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()
            val requester = FocusRequester()
            var clicks = 0
            var longClicks = 0
            var enabled by mutableStateOf(true)

            setContent {
                Box(
                    modifier =
                        Modifier
                            .size(10.dp)
                            .focusRequester(requester)
                            .focusable(interactionSource = source)
                            .handleHardwareInputEnter(
                                enabled = enabled,
                                interactionSource = source,
                                onLongClick = {
                                    longClicks++
                                    enabled = false
                                },
                                onClick = { clicks++ },
                                eventRepeatCount = { event ->
                                    if (event.key == Key.NumPadEnter) 1 else 0
                                },
                            ).testTag(TARGET_TAG),
                )
                LaunchedEffect(requester) { requester.requestFocus() }
                LaunchedEffect(source) {
                    source.interactions.collect { interactions += it }
                }
            }

            val target = onNodeWithTag(TARGET_TAG).assertIsFocused()
            target.performKeyInput {
                keyDown(Key.Enter)
                keyDown(Key.NumPadEnter)
            }
            waitUntil { longClicks == 1 }
            waitForIdle()

            target.performKeyInput {
                keyUp(Key.NumPadEnter)
                keyUp(Key.Enter)
            }
            runOnIdle { enabled = true }
            waitForIdle()

            target.performKeyInput {
                keyDown(Key.Enter)
                keyUp(Key.Enter)
            }
            waitForIdle()

            runOnIdle {
                val presses = interactions.filterIsInstance<PressInteraction.Press>()
                val releases = interactions.filterIsInstance<PressInteraction.Release>()
                assertEquals(2, presses.size)
                assertEquals(2, releases.size)
                assertSame(presses[0], releases[0].press)
                assertSame(presses[1], releases[1].press)
                assertEquals(1, longClicks)
                assertEquals(1, clicks)
            }
        }

    @Test
    fun leavingHardwareModeCancelsPendingDoubleClick() =
        runComposeUiTest {
            var clicks = 0
            var doubleClicks = 0
            var requiresHardwareInput by mutableStateOf(true)
            val requester = FocusRequester()
            val source = MutableInteractionSource()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides
                        PlatformInteractions(requiresHardwareInput = requiresHardwareInput),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .clickable(
                                    interactionSource = source,
                                    onDoubleClick = { doubleClicks++ },
                                    onClick = { clicks++ },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            target.performKeyInput {
                keyDown(Key.Enter)
                keyUp(Key.Enter)
            }
            runOnIdle { requiresHardwareInput = false }
            waitForIdle()
            runOnIdle { requiresHardwareInput = true }
            waitForIdle()
            delay(400)
            runOnIdle {
                assertEquals(0, clicks)
                assertEquals(0, doubleClicks)
            }
        }

    @Test
    fun leavingHardwareModeClearsCompletedLongClickState() =
        runComposeUiTest {
            var clicks = 0
            var longClicks = 0
            var requiresHardwareInput by mutableStateOf(true)
            val requester = FocusRequester()
            val source = MutableInteractionSource()

            setContent {
                CompositionLocalProvider(
                    LocalPlatformInteractions provides
                        PlatformInteractions(requiresHardwareInput = requiresHardwareInput),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(10.dp)
                                .focusRequester(requester)
                                .focusable()
                                .handleHardwareInputEnter(
                                    enabled = true,
                                    interactionSource = source,
                                    onLongClick = { longClicks++ },
                                    onClick = { clicks++ },
                                    observePlatformInteractions = true,
                                    eventRepeatCount = { event ->
                                        if (event.key == Key.NumPadEnter) 1 else 0
                                    },
                                ).testTag(TARGET_TAG),
                    )
                    LaunchedEffect(requester) { requester.requestFocus() }
                }
            }

            val target = onNodeWithTag(TARGET_TAG)
            target.performKeyInput { keyDown(Key.Enter) }
            target.performKeyInput { keyDown(Key.NumPadEnter) }
            waitForIdle()
            runOnIdle { requiresHardwareInput = false }
            waitForIdle()
            target.performKeyInput {
                keyUp(Key.NumPadEnter)
                keyUp(Key.Enter)
            }
            runOnIdle { assertEquals(1, longClicks) }

            runOnIdle {
                requiresHardwareInput = true
                requester.requestFocus()
            }
            waitForIdle()

            target.performKeyInput {
                keyDown(Key.Enter)
                keyUp(Key.Enter)
            }
            runOnIdle {
                assertEquals(1, longClicks)
                assertEquals(1, clicks)
            }
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
