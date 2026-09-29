// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class ButtonScreenshotTest : IosScreenshotTest() {
    @Test
    fun button() = captureScreenshot(advanceTimeByMillis = 0) { ButtonScene() }
}
