// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class CircularProgressScreenshotTest : IosScreenshotTest() {
    @Test
    fun circularProgress() = captureScreenshot(advanceTimeByMillis = 300) { CircularProgressScene() }
}
