package io.daio.wild.foundation

import android.content.pm.PackageManager.FEATURE_LEANBACK
import androidx.compose.runtime.CompositionLocalAccessorScope
import androidx.compose.ui.platform.LocalContext

internal actual fun CompositionLocalAccessorScope.getPlatformInteractions(): PlatformInteractions {
    val isTv = LocalContext.currentValue.packageManager.hasSystemFeature(FEATURE_LEANBACK)
    return PlatformInteractions(requiresHardwareInput = isTv)
}
