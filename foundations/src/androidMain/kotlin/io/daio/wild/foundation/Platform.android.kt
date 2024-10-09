package io.daio.wild.foundation

import android.content.pm.PackageManager.FEATURE_LEANBACK
import androidx.compose.runtime.CompositionLocalAccessorScope
import androidx.compose.ui.platform.LocalContext

internal actual fun CompositionLocalAccessorScope.getPlatform(): Platform {
    val isTv = LocalContext.currentValue.packageManager.hasSystemFeature(FEATURE_LEANBACK)
    return if (isTv) Platform.Tv else Platform.Mobile
}
