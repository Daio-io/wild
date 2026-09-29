// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class SmokeScreenshotTest : IosScreenshotTest() {
    @Test
    fun smoke() = captureScreenshot(advanceTimeByMillis = 0) { SmokeScene() }
}
