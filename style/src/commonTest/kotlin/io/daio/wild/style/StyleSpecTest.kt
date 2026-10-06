// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.compose.ui.graphics.Color
import io.daio.wild.foundation.ExperimentalWildApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalWildApi::class)
class StyleSpecTest {
    @Test
    fun equality_callbackIdentityOrder() {
        val base =
            StyleDefaults.style(
                colors = StyleDefaults.colors(backgroundColor = Color.Red),
            )
        val first: ComponentStyleScope.() -> Unit = { color = Color.Blue }
        val second: ComponentStyleScope.() -> Unit = { scale = 1.1f }

        val a = styleSpec(base, first).then(second)
        val b = styleSpec(base, first).then(second)
        val differentOrder = styleSpec(base, second).then(first)
        val differentIdentity = styleSpec(base) { color = Color.Blue }.then(second)
        val differentBase =
            styleSpec(
                StyleDefaults.style(colors = StyleDefaults.colors(backgroundColor = Color.Green)),
                first,
            ).then(second)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, differentOrder)
        assertNotEquals(a, differentIdentity)
        assertNotEquals(a, differentBase)
    }

    @Test
    fun contentColorFor_seedsChromeFromBaseBeforeCallbacks() {
        val baseBackground = Color.Red
        val base =
            StyleDefaults.style(
                colors =
                    StyleDefaults.colors(
                        backgroundColor = baseBackground,
                        contentColor = Color.Black,
                        focusedBackgroundColor = Color.Green,
                        focusedContentColor = Color.Yellow,
                    ),
            )
        // Same derivation resolveSpec allows: contentColor reads chrome seeded from base.
        val spec =
            styleSpec(base) {
                contentColor = color
            }

        assertEquals(baseBackground, spec.contentColorFor(enabled = true))
        assertEquals(Color.Green, spec.contentColorFor(enabled = true, focused = true))
    }
}
