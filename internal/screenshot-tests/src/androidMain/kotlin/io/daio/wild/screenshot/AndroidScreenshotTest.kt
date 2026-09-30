// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dropbox.differ.SimpleImageComparator
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
     * Captures the supplied composable. A non-null [captureName] writes to `screenshots/debug`;
     * omitting it preserves the standard `screenshots/android` output. Do not combine this with
     * [captureActivityScreenshot].
     *
     * @param captureName optional stable output filename without extension
     * @param advanceTimeByMillis optional virtual time to advance before capture
     * @param beforeCapture optional assertion or setup invoked immediately before capture
     * @param content composable content to capture
     * @since 0.4.0
     */
    fun captureScreenshot(
        captureName: String? = null,
        advanceTimeByMillis: Long? = null,
        beforeCapture: (() -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        if (advanceTimeByMillis != null) {
            composeRule.mainClock.autoAdvance = false
        }
        composeRule.setContent(content)
        capture(captureName, advanceTimeByMillis, beforeCapture)
    }

    /**
     * Captures the launched activity root. A non-null [captureName] writes to `screenshots/debug`;
     * omitting it preserves the standard `screenshots/android` output. Do not combine this with
     * [captureScreenshot].
     *
     * @param captureName optional stable output filename without extension
     * @param advanceTimeByMillis optional virtual time to advance before capture
     * @param beforeCapture optional assertion or setup invoked immediately before capture
     * @since 0.4.0
     */
    fun captureActivityScreenshot(
        captureName: String? = null,
        advanceTimeByMillis: Long? = null,
        beforeCapture: (() -> Unit)? = null,
    ) {
        if (advanceTimeByMillis != null) {
            composeRule.mainClock.autoAdvance = false
        }
        capture(captureName, advanceTimeByMillis, beforeCapture)
    }

    @OptIn(ExperimentalRoborazziApi::class)
    private fun capture(
        captureName: String?,
        advanceTimeByMillis: Long?,
        beforeCapture: (() -> Unit)?,
    ) {
        val outputDirectory = if (captureName == null) "screenshots/android" else "screenshots/debug"
        val outputFile = File("$outputDirectory/${captureName ?: roboOutputName()}.png")
        outputFile.parentFile?.mkdirs()
        if (advanceTimeByMillis != null) {
            // advanceTimeBy(0) does not run next-frame effects (LaunchedEffect / node
            // collectors). Advance one frame so focus interactions settle, then freeze.
            if (advanceTimeByMillis == 0L) {
                composeRule.mainClock.advanceTimeByFrame()
            } else {
                composeRule.mainClock.advanceTimeBy(advanceTimeByMillis)
            }
        }
        composeRule.waitForIdle()
        beforeCapture?.invoke()
        composeRule.onRoot().captureRoboImage(
            outputFile,
            RoborazziOptions(
                compareOptions =
                    RoborazziOptions.CompareOptions(
                        changeThreshold = 0f,
                        // Tolerate sub-pixel font AA across JDK/OS hosts (CI vs local).
                        imageComparator = SimpleImageComparator(maxDistance = 0.02f),
                    ),
            ),
        )
    }
}
