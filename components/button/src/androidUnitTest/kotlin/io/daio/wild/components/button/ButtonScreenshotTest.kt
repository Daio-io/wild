// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class ButtonScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun enabled() = captureScreenshot(advanceTimeByMillis = 0) { EnabledButton() }

    @Test
    fun disabled() = captureScreenshot(advanceTimeByMillis = 0) { DisabledButton() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedButton() }
}
