// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class IconScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun icon() = captureScreenshot(advanceTimeByMillis = 0) { IconScene() }
}
