// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class DividerScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun horizontal() = captureScreenshot(advanceTimeByMillis = 0) { HorizontalDividerScreenshot() }

    @Test
    fun vertical() = captureScreenshot(advanceTimeByMillis = 0) { VerticalDividerScreenshot() }
}
