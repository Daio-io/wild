// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class CheckboxScreenshotTest : DesktopScreenshotTest() {
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
}
