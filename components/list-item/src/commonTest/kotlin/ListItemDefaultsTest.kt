// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import kotlin.test.Test
import kotlin.test.assertEquals

class ListItemDefaultsTest {
    @Test
    fun defaultMinHeightIs48dp() {
        assertEquals(48.0, ListItemDefaults.defaultMinHeight.value.toDouble())
    }

    @Test
    fun defaultContentSpacingIs16dp() {
        assertEquals(16.0, ListItemDefaults.contentSpacing.value.toDouble())
    }
}
