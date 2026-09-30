// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.runtime.Composable
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
    /** Captures the supplied composable to `screenshots/desktop`. */
    @OptIn(ExperimentalRoborazziApi::class)
    fun captureScreenshot(
        captureName: String? = null,
        advanceTimeByMillis: Long? = null,
        content: @Composable () -> Unit,
    ) {
        val named = captureName != null
        val outputDirectory = if (named) "screenshots/jvm" else "screenshots/desktop"
        val outputFile = File("$outputDirectory/${captureName ?: roboOutputName()}.png")
        outputFile.parentFile?.mkdirs()
        val width = if (named) 1280 else 480
        val height = if (named) 720 else 480
        runDesktopComposeUiTest(width = width, height = height) {
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
