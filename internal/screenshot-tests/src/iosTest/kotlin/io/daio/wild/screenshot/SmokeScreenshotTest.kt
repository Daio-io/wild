// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import kotlin.test.Test

class SmokeScreenshotTest : IosScreenshotTest() {
    @Test
    fun smoke() = captureScreenshot("smoke") { SmokeScene() }
}
