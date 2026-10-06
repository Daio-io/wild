// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.container

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class ContainerScreenshotTest : IosScreenshotTest() {
    @Test
    fun static() = captureScreenshot(advanceTimeByMillis = 0) { StaticContainer() }

    @Test
    fun selected() = captureScreenshot(advanceTimeByMillis = 0) { SelectedContainer() }

    @Test
    fun disabled() = captureScreenshot(advanceTimeByMillis = 0) { DisabledContainer() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedContainer() }

    @Test
    fun spec() = captureScreenshot(advanceTimeByMillis = 0) { SpecContainer() }
}
