// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class ButtonScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun button() = captureScreenshot(advanceTimeByMillis = 0) { ButtonScene() }
}
