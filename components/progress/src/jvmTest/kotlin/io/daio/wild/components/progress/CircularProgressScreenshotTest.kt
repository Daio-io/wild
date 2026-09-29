// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class CircularProgressScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun circularProgress() = captureScreenshot(advanceTimeByMillis = 300) { CircularProgressScene() }
}
