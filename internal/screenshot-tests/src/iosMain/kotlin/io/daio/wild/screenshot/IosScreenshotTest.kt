// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

/** Base class for deterministic iOS screenshot tests. */
abstract class IosScreenshotTest {
    /** Captures the supplied composable to `screenshots/iosSimulatorArm64`. */
    @OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
    fun captureScreenshot(
        advanceTimeByMillis: Long? = null,
        content: @Composable () -> Unit,
    ) {
        val outputName = screenshotOutputName()
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
                filePath = "screenshots/iosSimulatorArm64/$outputName.png",
                roborazziOptions =
                    RoborazziOptions(
                        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
                    ),
            )
        }
    }

    /**
     * iOS Roborazzi requires an explicit file path and has no test-name generator, so derive
     * `ClassName.methodName` from the Native call stack.
     */
    @OptIn(kotlin.experimental.ExperimentalNativeApi::class)
    private fun screenshotOutputName(): String {
        val qualifiedName =
            this::class.qualifiedName
                ?: error("Screenshot test class must have a qualified name")
        val simpleName =
            this::class.simpleName
                ?: error("Screenshot test class must have a simple name")
        val marker = "kfun:$qualifiedName#"
        val frame =
            Throwable().getStackTrace().firstOrNull { it.contains(marker) }
                ?: error("Could not resolve screenshot test method name for $qualifiedName")
        val methodName = frame.substringAfter(marker).substringBefore('(')
        return "$simpleName.$methodName"
    }
}
