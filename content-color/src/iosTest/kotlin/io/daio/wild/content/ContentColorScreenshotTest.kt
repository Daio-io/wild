// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class ContentColorScreenshotTest : IosScreenshotTest() {
    @Test
    fun provided() = captureScreenshot(advanceTimeByMillis = 0) { ProvidedContentColor() }
}
