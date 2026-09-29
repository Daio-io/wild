// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class ContainerScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun container() = captureScreenshot(advanceTimeByMillis = 0) { ContainerScene() }
}
