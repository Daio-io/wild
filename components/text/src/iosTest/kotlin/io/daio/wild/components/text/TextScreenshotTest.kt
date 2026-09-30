// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class TextScreenshotTest : IosScreenshotTest() {
    @Test
    fun regular() = captureScreenshot(advanceTimeByMillis = 0) { RegularText() }

    @Test
    fun boldClipped() = captureScreenshot(advanceTimeByMillis = 0) { BoldClippedText() }
}
