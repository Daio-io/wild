// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class CircularProgressScreenshotTest : IosScreenshotTest() {
    @Test
    fun empty() = captureScreenshot(advanceTimeByMillis = 0) { CircularProgressEmpty() }

    @Test
    fun half() = captureScreenshot(advanceTimeByMillis = 0) { CircularProgressHalf() }

    @Test
    fun full() = captureScreenshot(advanceTimeByMillis = 0) { CircularProgressFull() }

    @Test
    fun indeterminate() = captureScreenshot(advanceTimeByMillis = 300) { CircularProgressIndeterminate() }
}
