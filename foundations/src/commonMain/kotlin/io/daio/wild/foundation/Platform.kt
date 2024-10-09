package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf

internal enum class Platform {
    Tv,
    Mobile,
    Web,
    Desktop,
}

internal val LocalPlatform = compositionLocalWithComputedDefaultOf { getPlatform() }

internal expect fun CompositionLocalAccessorScope.getPlatform(): Platform
