package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope

internal actual fun CompositionLocalAccessorScope.getPlatformInteractions(): PlatformInteractions =
    PlatformInteractions(requiresHardwareInput = false)
