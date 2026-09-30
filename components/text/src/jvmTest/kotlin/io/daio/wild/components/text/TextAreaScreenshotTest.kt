// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class TextAreaScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun threeLines() = captureScreenshot(advanceTimeByMillis = 0) { ThreeLineTextArea() }
}
