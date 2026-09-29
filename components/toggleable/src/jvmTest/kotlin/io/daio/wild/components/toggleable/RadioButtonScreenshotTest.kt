// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class RadioButtonScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun radioButton() = captureScreenshot(advanceTimeByMillis = 0) { RadioButtonScene() }
}
