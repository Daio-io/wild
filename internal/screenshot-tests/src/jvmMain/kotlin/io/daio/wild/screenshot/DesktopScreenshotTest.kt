// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.roboOutputName
import io.github.takahirom.roborazzi.captureRoboImage
import java.io.File

/** Base class for deterministic desktop screenshot tests. */
@OptIn(ExperimentalTestApi::class)
abstract class DesktopScreenshotTest {
    /** Captures the supplied composable. */
    @OptIn(ExperimentalRoborazziApi::class)
    fun captureScreenshot(
        advanceTimeByMillis: Long? = null,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        val outputFile = File("screenshots/desktop/${roboOutputName()}.png")
        outputFile.parentFile?.mkdirs()
        runDesktopComposeUiTest(width = 480, height = 480) {
            if (advanceTimeByMillis != null) {
                mainClock.autoAdvance = false
            }
            setContent(content)
            if (advanceTimeByMillis != null) {
                mainClock.advanceTimeBy(advanceTimeByMillis)
            }
            waitForIdle()
            onRoot().captureRoboImage(
                outputFile,
                RoborazziOptions(
                    compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
                ),
            )
        }
    }
}
