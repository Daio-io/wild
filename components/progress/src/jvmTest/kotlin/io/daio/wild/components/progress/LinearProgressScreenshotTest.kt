// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class LinearProgressScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun empty() = captureScreenshot(advanceTimeByMillis = 0) { LinearProgressEmpty() }

    @Test
    fun half() = captureScreenshot(advanceTimeByMillis = 0) { LinearProgressHalf() }

    @Test
    fun full() = captureScreenshot(advanceTimeByMillis = 0) { LinearProgressFull() }

    @Test
    fun indeterminate() = captureScreenshot(advanceTimeByMillis = 300) { LinearProgressIndeterminate() }
}
