// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.icon

import androidx.activity.ComponentActivity
import io.daio.wild.screenshot.AndroidScreenshotTest
import kotlin.test.Test

class IconScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun vector() = captureScreenshot(advanceTimeByMillis = 0) { VectorIcon() }

    @Test
    fun bitmap() = captureScreenshot(advanceTimeByMillis = 0) { BitmapIcon() }
}
