// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf

@Immutable
class PlatformInteractions internal constructor(
    /**
     * Indicates the platform requires a hardware input such as a remote control in order to
     * respond to user interactions. Commonly Tv devices.
     */
    val requiresHardwareInput: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PlatformInteractions

        return requiresHardwareInput == other.requiresHardwareInput
    }

    override fun hashCode(): Int {
        return requiresHardwareInput.hashCode()
    }
}

/**
 * Local to expose interaction capabilities of the current platform.
 */
@ExperimentalWildApi
val LocalPlatformInteractions = compositionLocalWithComputedDefaultOf { getPlatformInteractions() }

internal expect fun CompositionLocalAccessorScope.getPlatformInteractions(): PlatformInteractions
