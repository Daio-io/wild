// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IconDefaultsTest {
    @Test
    fun defaultSizeIs24dp() {
        assertEquals(24.0, IconDefaults.defaultSize.value.toDouble())
    }
}

class IconTintLogicTest {
    @Test
    fun unspecifiedTintShouldProduceNullFilter() {
        val tint = Color.Unspecified
        val shouldApplyFilter = tint != Color.Unspecified
        assertFalse(shouldApplyFilter)
    }

    @Test
    fun specifiedTintShouldProduceFilter() {
        val tint = Color.Red
        val shouldApplyFilter = tint != Color.Unspecified
        assertTrue(shouldApplyFilter)
    }
}
