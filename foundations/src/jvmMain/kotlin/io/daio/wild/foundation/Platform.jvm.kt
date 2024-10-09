package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope

// TODO would need more detection to be classified as Desktop.
internal actual fun CompositionLocalAccessorScope.getPlatform(): Platform = Platform.Desktop
