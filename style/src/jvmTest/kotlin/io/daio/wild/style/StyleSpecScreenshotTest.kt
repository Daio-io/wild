// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.style

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class StyleSpecScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedStyleSpec() }
}
