// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class ListItemScreenshotTest : IosScreenshotTest() {
    @Test
    fun listItem() = captureScreenshot(advanceTimeByMillis = 0) { ListItemScene() }
}
