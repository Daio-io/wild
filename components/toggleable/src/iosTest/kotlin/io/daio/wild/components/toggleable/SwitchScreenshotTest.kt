// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class SwitchScreenshotTest : IosScreenshotTest() {
    @Test
    fun off() = captureScreenshot(advanceTimeByMillis = 0) { OffSwitch() }

    @Test
    fun on() = captureScreenshot(advanceTimeByMillis = 0) { OnSwitch() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedSwitch() }
}
