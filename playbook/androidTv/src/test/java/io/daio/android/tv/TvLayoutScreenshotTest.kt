// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.AndroidScreenshotTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "landscape-xlarge")
class TvLayoutScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    private fun captureTvScreenshot(name: String, mode: String, itemsType: String) =
        captureScreenshot(name) {
            Box(Modifier.size(1920.dp, 1080.dp)) {
                TvLayout(mode = mode, itemsType = itemsType)
            }
        }

    @Test
    fun list() = captureTvScreenshot("tv-list", "list", "wild_container")

    @Test
    fun grid() = captureTvScreenshot("tv-grid", "grid", "wild_container")

    @Test
    fun focused() =
        captureScreenshot(
            captureName = "tv-focus",
            beforeCapture = {
                composeRule.onNodeWithContentDescription("benchmark-item-0-0").assertIsFocused()
            },
        ) {
            Box(Modifier.size(1920.dp, 1080.dp)) {
                TvLayout(mode = "focus_flip", itemsType = "wild_clickable")
            }
        }
}
