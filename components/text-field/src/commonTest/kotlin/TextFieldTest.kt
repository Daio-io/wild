// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.textfield

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.daio.wild.components.text.LocalTextStyle
import io.daio.wild.content.LocalContentColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
class TextFieldTest {
    @Test
    fun rendersProgrammaticStateText() =
        runComposeUiTest {
            setContent {
                TextField(
                    state = TextFieldState(initialText = "Hello, Wild!"),
                    modifier = Modifier.testTag("text-field"),
                )
            }

            onNodeWithTag("text-field").assertTextEquals("Hello, Wild!")
        }

    @Test
    fun textInputMutatesState() =
        runComposeUiTest {
            val state = TextFieldState()

            setContent {
                TextField(
                    state = state,
                    modifier = Modifier.testTag("text-field"),
                )
            }

            onNodeWithTag("text-field").performTextInput("Typed")

            runOnIdle { assertEquals("Typed", state.text.toString()) }
        }

    @Test
    fun disabledRejectsInputAndExposesDisabledSemantics() =
        runComposeUiTest {
            val state = TextFieldState(initialText = "Initial")

            setContent {
                TextField(
                    state = state,
                    enabled = false,
                    modifier = Modifier.testTag("text-field"),
                )
            }

            onNodeWithTag("text-field").assertIsNotEnabled()

            assertFailsWith<AssertionError> {
                onNodeWithTag("text-field").performTextInput("Typed")
            }

            runOnIdle { assertEquals("Initial", state.text.toString()) }
        }

    @Test
    fun readOnlyRemainsFocusableButRejectsEdits() =
        runComposeUiTest {
            val state = TextFieldState(initialText = "Initial")

            setContent {
                TextField(
                    state = state,
                    readOnly = true,
                    modifier = Modifier.testTag("text-field"),
                )
            }

            onNodeWithTag("text-field")
                .assertIsEnabled()
                .assert(isNotEditable())
                .performSemanticsAction(SemanticsActions.RequestFocus)
                .assertIsFocused()

            // SetSelection remains available for selection; SetText does not.
            onNodeWithTag("text-field").assert(hasSetSelectionAction())

            assertFailsWith<AssertionError> {
                onNodeWithTag("text-field").performTextInput("Typed")
            }

            runOnIdle { assertEquals("Initial", state.text.toString()) }
        }

    @Test
    fun fallsBackToLocalTextStyleAndContentColor() =
        runComposeUiTest {
            var observedColor = Color.Unspecified
            var observedFontSize = 0.sp

            setContent {
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle(fontSize = 28.sp),
                    LocalContentColor provides Color.Red,
                ) {
                    TextField(
                        state = TextFieldState(initialText = "Styled"),
                        modifier = Modifier.testTag("text-field"),
                        onTextLayout = { getResult ->
                            getResult()?.layoutInput?.style?.let { style ->
                                observedColor = style.color
                                observedFontSize = style.fontSize
                            }
                        },
                    )
                }
            }

            waitForIdle()
            runOnIdle {
                assertEquals(28.sp, observedFontSize)
                assertEquals(Color.Red, observedColor)
            }
        }

    @Test
    fun decoratorIsInvokedAndComposesInnerFieldOnce() =
        runComposeUiTest {
            var decoratorCompositions = 0
            var innerCompositions = 0

            setContent {
                TextField(
                    state = TextFieldState(initialText = "Decorated"),
                    modifier = Modifier.testTag("text-field"),
                    decorator =
                        TextFieldDecorator { innerTextField ->
                            decoratorCompositions++
                            Box(modifier = Modifier.testTag("decorator")) {
                                innerCompositions++
                                innerTextField()
                            }
                        },
                )
            }

            onNodeWithTag("decorator", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("text-field").assertTextEquals("Decorated")
            runOnIdle {
                assertEquals(1, decoratorCompositions)
                assertEquals(1, innerCompositions)
            }
        }

    @Test
    fun forwardsInputAndOutputTransformations() =
        runComposeUiTest {
            val state = TextFieldState()

            setContent {
                TextField(
                    state = state,
                    modifier = Modifier.testTag("text-field"),
                    inputTransformation = InputTransformation.maxLength(3),
                    outputTransformation =
                        OutputTransformation {
                            if (length > 0) insert(0, "[")
                            if (length > 0) insert(length, "]")
                        },
                )
            }

            onNodeWithTag("text-field").performTextInput("ABC")
            runOnIdle { assertEquals("ABC", state.text.toString()) }

            onNodeWithTag("text-field").performTextInput("D")
            runOnIdle { assertEquals("ABC", state.text.toString()) }

            onNodeWithTag("text-field").assertTextEquals("[ABC]")
        }

    @Test
    fun textAreaRejectsInvalidMinLines() =
        runComposeUiTest {
            var threw = false
            try {
                setContent {
                    TextArea(
                        state = TextFieldState(),
                        minLines = 0,
                    )
                }
            } catch (error: IllegalArgumentException) {
                threw = true
            }
            assertTrue(threw)
        }

    @Test
    fun textAreaRejectsMaxLinesBelowMinLines() =
        runComposeUiTest {
            var threw = false
            try {
                setContent {
                    TextArea(
                        state = TextFieldState(),
                        minLines = 3,
                        maxLines = 2,
                    )
                }
            } catch (error: IllegalArgumentException) {
                threw = true
            }
            assertTrue(threw)
        }

    @Test
    fun textAreaAcceptsMultilineInput() =
        runComposeUiTest {
            val state = TextFieldState()
            setContent {
                TextArea(
                    state = state,
                    modifier = Modifier.testTag("text-area"),
                )
            }

            onNodeWithTag("text-area").performTextInput("line1\nline2")
            runOnIdle { assertEquals("line1\nline2", state.text.toString()) }
            assertEquals(3, TextFieldDefaults.textAreaMinLines)
        }

    @Test
    fun suppliedInteractionSourceReceivesFocusInteractions() =
        runComposeUiTest {
            val source = CountingMutableInteractionSource()

            setContent {
                TextField(
                    state = TextFieldState(initialText = "Focus me"),
                    modifier = Modifier.testTag("text-field"),
                    interactionSource = source,
                )
            }

            onNodeWithTag("text-field").performSemanticsAction(SemanticsActions.RequestFocus)
            waitForIdle()

            runOnIdle {
                assertTrue(source.emittedInteractions.any { it is FocusInteraction.Focus })
            }
        }
}

private fun isNotEditable(): SemanticsMatcher =
    SemanticsMatcher("is not editable") { node ->
        SemanticsProperties.IsEditable in node.config &&
            !node.config[SemanticsProperties.IsEditable]
    }

private fun hasSetSelectionAction(): SemanticsMatcher = SemanticsMatcher.keyIsDefined(SemanticsActions.SetSelection)

private class CountingMutableInteractionSource : MutableInteractionSource {
    private val delegate = MutableInteractionSource()

    val emittedInteractions = mutableListOf<androidx.compose.foundation.interaction.Interaction>()

    override val interactions: Flow<androidx.compose.foundation.interaction.Interaction> =
        flow {
            delegate.interactions.collect { interaction -> emit(interaction) }
        }

    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {
        emittedInteractions += interaction
        delegate.emit(interaction)
    }

    override fun tryEmit(interaction: androidx.compose.foundation.interaction.Interaction): Boolean {
        emittedInteractions += interaction
        return delegate.tryEmit(interaction)
    }
}
