// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import kotlin.test.Test
import kotlin.test.assertEquals

class DividerDefaultsTest {
    @Test
    fun defaultThicknessIs1dp() {
        assertEquals(1.0, DividerDefaults.thickness.value.toDouble())
    }
}
