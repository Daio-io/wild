// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.screenshot

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.RoborazziOptions

/**
 * Shared Roborazzi compare options for host screenshot verification.
 *
 * Keeps bit-exact failure for real visual changes (`changeThreshold = 0`) while allowing the small
 * per-pixel antialiasing deltas that show up across JDK/Skia hosts (CI vs local).
 */
internal fun screenshotCompareOptions(): RoborazziOptions.CompareOptions =
    RoborazziOptions.CompareOptions(
        changeThreshold = 0f,
        imageComparator =
            SimpleImageComparator(
                // Default Differ maxDistance (0.007) treats single-channel AA deltas of ~2/255 as
                // failures. 0.02 still rejects real color/layout regressions.
                maxDistance = 0.02f,
            ),
    )
