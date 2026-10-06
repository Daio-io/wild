// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.toggleable

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class RadioButtonScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun unchecked() = captureScreenshot(advanceTimeByMillis = 0) { UncheckedRadioButton() }

    @Test
    fun checked() = captureScreenshot(advanceTimeByMillis = 0) { CheckedRadioButton() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedRadioButton() }

    @Test
    fun spec() = captureScreenshot(advanceTimeByMillis = 0) { SpecSelectableRadioButton() }
}
