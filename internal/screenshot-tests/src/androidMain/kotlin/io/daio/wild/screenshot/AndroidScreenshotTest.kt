// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.roboOutputName
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Base class for deterministic Robolectric screenshot tests. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel5)
abstract class AndroidScreenshotTest<A : ComponentActivity>(activityClass: Class<A>) {
    @get:Rule
    val composeRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A> = createAndroidComposeRule(activityClass)

    @get:Rule
    val roborazziRule = RoborazziRule()

    /**
     * Captures the supplied composable. Named captures are matrix cases written to `screenshots/debug`;
     * unnamed captures preserve the legacy `screenshots/android` output. Do not combine this with
     * [captureActivityScreenshot].
     */
    fun captureScreenshot(
        captureName: String? = null,
        advanceTimeByMillis: Long? = null,
        content: @Composable () -> Unit,
    ) {
        if (advanceTimeByMillis != null) {
            composeRule.mainClock.autoAdvance = false
        }
        composeRule.setContent(content)
        capture(captureName, advanceTimeByMillis)
    }

    /** Captures the launched activity root. Do not combine this with [captureScreenshot]. */
    fun captureActivityScreenshot(advanceTimeByMillis: Long? = null) {
        if (advanceTimeByMillis != null) {
            composeRule.mainClock.autoAdvance = false
        }
        capture(null, advanceTimeByMillis)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun capture(
        captureName: String?,
        advanceTimeByMillis: Long?,
    ) {
        val outputDirectory = if (captureName == null) "screenshots/android" else "screenshots/debug"
        val outputFile = File("$outputDirectory/${captureName ?: roboOutputName()}.png")
        outputFile.parentFile?.mkdirs()
        if (advanceTimeByMillis != null) {
            composeRule.mainClock.advanceTimeBy(advanceTimeByMillis)
        }
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage(
            outputFile,
            RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
            ),
        )
    }
}
