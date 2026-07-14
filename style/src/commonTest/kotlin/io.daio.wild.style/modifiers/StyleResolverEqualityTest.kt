// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.modifiers

import androidx.compose.ui.graphics.Color
import io.daio.wild.style.StyleDefaults
import io.daio.wild.style.StyleScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StyleResolverEqualityTest {
    @Test
    fun valueResolversWithEqualStylesAreEqual() {
        val style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red))
        val a = StyleResolver.Value(style)
        val b = StyleResolver.Value(style.copy())
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun valueElementsEqualWhenStyleEnabledSelectedMatch() {
        val style = StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Blue))
        val a = StyleScopeParentElement(enabled = true, selected = false, resolver = StyleResolver.Value(style))
        val b = StyleScopeParentElement(enabled = true, selected = false, resolver = StyleResolver.Value(style.copy()))
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun valueElementsUnequalWhenStyleDiffers() {
        val a =
            StyleScopeParentElement(
                resolver =
                    StyleResolver.Value(
                        StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Red)),
                    ),
            )
        val b =
            StyleScopeParentElement(
                resolver =
                    StyleResolver.Value(
                        StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Green)),
                    ),
            )
        assertNotEquals(a, b)
    }

    @Test
    fun blockElementsUseBlockIdentity() {
        val block: StyleScope.() -> Unit = { color = Color.Red }
        val a = StyleScopeParentElement(resolver = StyleResolver.Block(block))
        val b = StyleScopeParentElement(resolver = StyleResolver.Block(block))
        val c = StyleScopeParentElement(resolver = StyleResolver.Block { color = Color.Red })
        assertEquals(a, b)
        assertNotEquals(a, c)
    }
}
