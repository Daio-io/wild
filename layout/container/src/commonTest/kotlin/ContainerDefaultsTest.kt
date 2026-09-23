// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import io.daio.wild.style.StyleDefaults
import kotlin.test.Test
import kotlin.test.assertSame

class ContainerDefaultsTest {
    @Test
    fun defaultStyleUsesCachedStyleDefaultsNone() {
        assertSame(StyleDefaults.None, ContainerDefaults.style())
    }
}
