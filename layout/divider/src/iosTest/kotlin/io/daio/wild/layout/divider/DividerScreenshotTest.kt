// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class DividerScreenshotTest : IosScreenshotTest() {
    @Test
    fun dividers() = captureScreenshot(advanceTimeByMillis = 0) { DividerScene() }
}
