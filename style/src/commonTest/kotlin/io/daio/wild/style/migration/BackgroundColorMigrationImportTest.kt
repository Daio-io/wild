// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style.migration

import androidx.compose.ui.graphics.Color
import io.daio.wild.style.DefaultStyleScope
import io.daio.wild.style.StyleScope
import kotlin.test.Test
import kotlin.test.assertEquals

class BackgroundColorMigrationImportTest {
    @Test
    fun backgroundColor_isReceiverMember_withoutExtensionImport() {
        val scope: StyleScope = DefaultStyleScope()
        // Member access via receiver only — must not require importing an extension property.
        scope.apply {
            backgroundColor = Color.Magenta
        }
        assertEquals(Color.Magenta, scope.color)
        assertEquals(Color.Magenta, scope.backgroundColor)
    }
}
