// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class CheckboxScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun unchecked() = captureScreenshot(advanceTimeByMillis = 0) { UncheckedCheckbox() }

    @Test
    fun checked() = captureScreenshot(advanceTimeByMillis = 0) { CheckedCheckbox() }

    @Test
    fun indeterminate() = captureScreenshot(advanceTimeByMillis = 0) { IndeterminateCheckbox() }

    @Test
    fun disabledChecked() = captureScreenshot(advanceTimeByMillis = 0) { DisabledCheckedCheckbox() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedCheckbox() }

    @Test
    fun spec() = captureScreenshot(advanceTimeByMillis = 0) { SpecToggleableCheckbox() }
}
