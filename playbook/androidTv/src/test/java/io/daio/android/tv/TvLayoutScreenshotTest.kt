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
import org.robolectric.annotation.Config

@Config(sdk = [35], qualifiers = "w1920dp-h1080dp-land")
class TvLayoutScreenshotTest : AndroidScreenshotTest<ComponentActivity>(ComponentActivity::class.java) {
    @Test
    fun list() =
        captureScreenshot("tv-list") {
            Box(Modifier.size(1920.dp, 1080.dp)) {
                TvLayout(mode = "list", itemsType = "wild_container")
            }
        }

    @Test
    fun grid() =
        captureScreenshot("tv-grid") {
            Box(Modifier.size(1920.dp, 1080.dp)) {
                TvLayout(mode = "grid", itemsType = "wild_container")
            }
        }

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
