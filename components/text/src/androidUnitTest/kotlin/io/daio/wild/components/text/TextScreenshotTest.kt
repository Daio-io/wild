// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class TextScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun regular() = captureScreenshot(advanceTimeByMillis = 0) { RegularText() }

    @Test
    fun boldClipped() = captureScreenshot(advanceTimeByMillis = 0) { BoldClippedText() }
}
