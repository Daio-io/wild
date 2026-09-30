// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.text

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class TextFieldScreenshotTest : IosScreenshotTest() {
    @Test
    fun empty() = captureScreenshot(advanceTimeByMillis = 0) { EmptyTextField() }

    @Test
    fun readOnly() = captureScreenshot(advanceTimeByMillis = 0) { ReadOnlyTextField() }

    @Test
    fun disabled() = captureScreenshot(advanceTimeByMillis = 0) { DisabledTextField() }
}
