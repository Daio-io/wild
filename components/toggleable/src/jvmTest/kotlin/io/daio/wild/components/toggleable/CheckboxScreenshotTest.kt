// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class CheckboxScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun checkbox() = captureScreenshot(advanceTimeByMillis = 0) { CheckboxScene() }
}
