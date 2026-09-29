// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class ListItemScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun listItem() = captureScreenshot(advanceTimeByMillis = 0) { ListItemScene() }
}
