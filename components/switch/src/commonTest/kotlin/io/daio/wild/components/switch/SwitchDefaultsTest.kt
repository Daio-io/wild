// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.switch

import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertSame

class SwitchDefaultsTest {
    @Test
    fun defaultStyleMatchesStyleDefaultsNone() {
        assertSame(StyleDefaults.None, SwitchDefaults.style())
    }
}
