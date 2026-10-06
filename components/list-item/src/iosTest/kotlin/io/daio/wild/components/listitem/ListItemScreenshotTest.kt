// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.listitem

import io.daio.wild.screenshot.IosScreenshotTest
import kotlin.test.Test

class ListItemScreenshotTest : IosScreenshotTest() {
    @Test
    fun enabled() = captureScreenshot(advanceTimeByMillis = 0) { EnabledListItem() }

    @Test
    fun selected() = captureScreenshot(advanceTimeByMillis = 0) { SelectedListItem() }

    @Test
    fun disabled() = captureScreenshot(advanceTimeByMillis = 0) { DisabledListItem() }

    @Test
    fun focused() = captureScreenshot(advanceTimeByMillis = 0) { FocusedListItem() }

    @Test
    fun spec() = captureScreenshot(advanceTimeByMillis = 0) { SpecListItem() }
}
