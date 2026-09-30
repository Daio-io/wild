// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android

import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import io.daio.wild.screenshot.AndroidScreenshotTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class AndroidPlaybookScreenshotTest : AndroidScreenshotTest<MainActivity>(MainActivity::class.java) {
    @Test
    fun mobileLanding() = captureActivityScreenshot("android-mobile-landing")
}
