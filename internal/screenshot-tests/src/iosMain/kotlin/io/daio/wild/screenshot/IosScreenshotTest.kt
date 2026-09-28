// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

/** Base class for deterministic iOS screenshot tests. */
abstract class IosScreenshotTest {
    /** Captures the supplied composable. */
    @OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
    fun captureScreenshot(
        name: String,
        advanceTimeByMillis: Long? = null,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        runComposeUiTest {
            if (advanceTimeByMillis != null) {
                mainClock.autoAdvance = false
            }
            setContent(content)
            if (advanceTimeByMillis != null) {
                mainClock.advanceTimeBy(advanceTimeByMillis)
            }
            waitForIdle()
            onRoot().captureRoboImage(
                this,
                filePath = "iosSimulatorArm64/$name.png",
                roborazziOptions =
                    RoborazziOptions(
                        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
                    ),
            )
        }
    }
}
