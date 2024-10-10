package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf

@Immutable
internal data class PlatformInteractions(
    /**
     * Indicates the platform requires a hardware input such as a remote control in order to
     * respond to user interactions. Commonly Tv devices.
     */
    val requiresHardwareInput: Boolean,
)

internal val LocalPlatformInteractions =
    compositionLocalWithComputedDefaultOf { getPlatformInteractions() }

internal expect fun CompositionLocalAccessorScope.getPlatformInteractions(): PlatformInteractions
