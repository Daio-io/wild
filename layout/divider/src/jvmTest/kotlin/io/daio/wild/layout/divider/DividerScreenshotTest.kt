// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.layout.divider

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class DividerScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun horizontal() = captureScreenshot(advanceTimeByMillis = 0) { HorizontalDividerScreenshot() }

    @Test
    fun vertical() = captureScreenshot(advanceTimeByMillis = 0) { VerticalDividerScreenshot() }
}
