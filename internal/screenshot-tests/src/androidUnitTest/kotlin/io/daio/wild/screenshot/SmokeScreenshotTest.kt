// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.activity.ComponentActivity
import kotlin.test.Test

class SmokeScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun smoke() = captureScreenshot(advanceTimeByMillis = 0) { SmokeScene() }
}
