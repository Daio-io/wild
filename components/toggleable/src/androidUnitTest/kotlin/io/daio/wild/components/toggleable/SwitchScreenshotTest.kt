// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class SwitchScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun off() = captureScreenshot(advanceTimeByMillis = 0) { OffSwitch() }

    @Test
    fun on() = captureScreenshot(advanceTimeByMillis = 0) { OnSwitch() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedSwitch() }
}
