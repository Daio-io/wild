// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.daio.wild.style.modifiers.StyleRecorder
import io.daio.wild.style.modifiers.StyleResolver
import io.daio.wild.style.modifiers.StyleScopeParentElement
import io.daio.wild.style.modifiers.recordStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StyleLambdaInputApiTest {
    @Test
    fun noStyle_valueStyle_nullStyle_nullStyleBlock_lambda_trailingOnClick_resolve() {
        val source = MutableInteractionSource()
        val style = StyleDefaults.None
        val block: StyleScope.() -> Unit = { color = Color.Red }

        // clickable call-site shapes must remain unambiguous.
        Modifier.clickable(onClick = {})
        Modifier.clickable(style = style, onClick = {})
        Modifier.clickable(style = null, onClick = {})
        Modifier.clickable(styleBlock = null, onClick = {})
        Modifier.clickable(styleBlock = block, onClick = {})
        Modifier.clickable(styleBlock = { color = Color.Blue }, onClick = {})
        Modifier.clickable(interactionSource = source, styleBlock = block, onClick = {})

        Modifier.selectable(selected = true, onClick = {})
        Modifier.selectable(selected = true, style = style, onClick = {})
        Modifier.selectable(selected = true, style = null, onClick = {})
        Modifier.selectable(selected = true, styleBlock = null, onClick = {})
        Modifier.selectable(selected = true, styleBlock = block, onClick = {})
        Modifier.selectable(selected = true, styleBlock = { color = Color.Blue }, onClick = {})
        Modifier.selectable(selected = true, interactionSource = source, styleBlock = block, onClick = {})

        Modifier.interactable(onClick = {})
        Modifier.interactable(style = style, onClick = {})
        Modifier.interactable(style = null, onClick = {})
        Modifier.interactable(styleBlock = null, onClick = {})
        Modifier.interactable(styleBlock = block, onClick = {})
        Modifier.interactable(styleBlock = { color = Color.Blue }, onClick = {})
        Modifier.interactable(interactionSource = source, styleBlock = block, onClick = {})
        Modifier.interactable(selected = true, styleBlock = block, onClick = {})
    }

    @Test
    fun nullStyleBlock_installsNoStyleObserver() {
        val source = MutableInteractionSource()

        assertNull(
            Modifier.clickable(interactionSource = source, styleBlock = null, onClick = {})
                .findStyleParent(),
        )
        assertNull(
            Modifier.selectable(selected = true, interactionSource = source, styleBlock = null, onClick = {})
                .findStyleParent(),
        )
        assertNull(
            Modifier.interactable(interactionSource = source, styleBlock = null, onClick = {})
                .findStyleParent(),
        )
        assertNull(
            Modifier.interactable(selected = true, interactionSource = source, styleBlock = null, onClick = {})
                .findStyleParent(),
        )
    }

    @Test
    fun styleBlock_observesViaInteractionStyleBlock() =
        runComposeUiTest {
            val source = MutableInteractionSource()
            val block: StyleScope.() -> Unit = { color = Color.Red }
            val recorder = StyleRecorder()

            val parent =
                Modifier.clickable(interactionSource = source, styleBlock = block, onClick = {})
                    .findStyleParent()
            assertNotNull(parent)
            assertIs<StyleResolver.Block>(parent.resolver)
            assertEquals(StyleResolver.Block(block), parent.resolver)

            setContent {
                Box(
                    Modifier
                        .size(1.dp)
                        .clickable(interactionSource = source, styleBlock = block, onClick = {})
                        .recordStyle(recorder),
                )
            }
            waitForIdle()
            assertEquals(Color.Red, recorder.last.color)
        }

    @Test
    fun interactable_selected_routesToSelectableStyleBlock() {
        val source = MutableInteractionSource()
        val block: StyleScope.() -> Unit = { color = Color.Green }

        val parent =
            Modifier.interactable(
                selected = true,
                interactionSource = source,
                styleBlock = block,
                onClick = {},
            ).findStyleParent()

        assertNotNull(parent)
        assertTrue(parent.selected)
        assertEquals(StyleResolver.Block(block), parent.resolver)
    }

    @Test
    fun nullableBlockVariable_replaceWithTargetCompiles() {
        val source = MutableInteractionSource()
        val style: (StyleScope.() -> Unit)? = { color = Color.Red }
        val onLongClick: (() -> Unit)? = null
        val onDoubleClick: (() -> Unit)? = null
        val block: StyleScope.() -> Unit = style ?: {}

        // ReplaceWith targets for deprecated lambda helpers must compile with nullable locals.
        Modifier.clickable(
            enabled = true,
            interactionSource = source,
            role = null,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            styleBlock = style,
            onClick = {},
        )
        Modifier.selectable(
            selected = true,
            enabled = true,
            interactionSource = source,
            role = null,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            styleBlock = style,
            onClick = {},
        )
        Modifier.interactable(
            enabled = true,
            selected = null,
            interactionSource = source,
            role = null,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
            styleBlock = style,
            onClick = {},
        )
        Modifier.interactionStyle(
            interactionSource = source,
            enabled = true,
            selected = false,
            block = block,
        )
    }

    @Test
    fun backgroundColor_aliasesColorOnStyleScope() {
        val scope = DefaultStyleScope()
        scope.backgroundColor = Color.Magenta
        assertEquals(Color.Magenta, scope.color)
        assertEquals(Color.Magenta, scope.backgroundColor)

        scope.color = Color.Cyan
        assertEquals(Color.Cyan, scope.backgroundColor)
    }
}

private fun Modifier.findStyleParent(): StyleScopeParentElement? {
    var found: StyleScopeParentElement? = null
    foldIn(Unit) { _, element ->
        if (element is StyleScopeParentElement && found == null) {
            found = element
        }
    }
    return found
}
