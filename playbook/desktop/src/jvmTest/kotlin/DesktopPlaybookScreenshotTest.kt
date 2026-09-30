// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.desktop

import io.daio.common.CustomDesignSystemApp
import io.daio.wild.screenshot.DesktopScreenshotTest
import kotlin.test.Test

class DesktopPlaybookScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun desktopLanding() =
        captureScreenshot("desktop-landing") {
            CustomDesignSystemApp()
        }
}
