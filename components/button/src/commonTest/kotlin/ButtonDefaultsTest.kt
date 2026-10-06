// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import io.daio.wild.foundation.ExperimentalWildApi
import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertSame

class ButtonDefaultsTest {
    @Test
    fun defaultStyleUsesCachedStyleDefaultsNone() {
        assertSame(StyleDefaults.None, ButtonDefaults.style())
    }

    @OptIn(ExperimentalWildApi::class)
    @Test
    fun defaultStyleSpecReusesCachedInstance() {
        assertSame(ButtonDefaults.styleSpec(), ButtonDefaults.styleSpec())
        assertSame(ButtonDefaults.style(), ButtonDefaults.styleSpec().base)
    }
}
