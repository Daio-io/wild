// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class RadioButtonScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun unchecked() = captureScreenshot(advanceTimeByMillis = 0) { UncheckedRadioButton() }

    @Test
    fun checked() = captureScreenshot(advanceTimeByMillis = 0) { CheckedRadioButton() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedRadioButton() }
}
