// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class RadioButtonScreenshotTest : IosScreenshotTest() {
    @Test
    fun radioButton() = captureScreenshot(advanceTimeByMillis = 0) { RadioButtonScene() }
}
