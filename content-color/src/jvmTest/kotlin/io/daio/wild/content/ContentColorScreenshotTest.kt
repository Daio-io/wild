// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class ContentColorScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun provided() = captureScreenshot(advanceTimeByMillis = 0) { ProvidedContentColor() }
}
