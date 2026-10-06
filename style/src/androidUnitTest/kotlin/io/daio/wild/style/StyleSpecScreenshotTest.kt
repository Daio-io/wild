// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class StyleSpecScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedStyleSpec() }
}
