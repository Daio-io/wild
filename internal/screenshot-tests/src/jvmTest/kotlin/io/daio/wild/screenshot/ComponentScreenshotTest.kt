// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import io.daio.wild.screenshot.scenes.ControlsScene
import io.daio.wild.screenshot.scenes.FoundationScene
import io.daio.wild.screenshot.scenes.InputProgressScene
import io.daio.wild.screenshot.scenes.LayoutTextIconScene
import kotlin.test.Test

class ComponentScreenshotTest : DesktopScreenshotTest() {
    @Test
    fun foundations() = captureScreenshot("foundations") { FoundationScene() }

    @Test
    fun layoutTextIcon() = captureScreenshot("layout-text-icon") { LayoutTextIconScene() }

    @Test
    fun controls() = captureScreenshot("controls") { ControlsScene() }

    @Test
    fun inputProgress() = captureScreenshot("input-progress", advanceTimeByMillis = 300) { InputProgressScene() }
}
