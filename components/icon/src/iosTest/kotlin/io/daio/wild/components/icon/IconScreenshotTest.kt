// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class IconScreenshotTest : IosScreenshotTest() {
    @Test
    fun icon() = captureScreenshot(advanceTimeByMillis = 0) { IconScene() }
}
