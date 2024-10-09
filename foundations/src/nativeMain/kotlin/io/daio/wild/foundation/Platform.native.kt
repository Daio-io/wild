package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope

// TODO this should not be native.
internal actual fun CompositionLocalAccessorScope.getPlatform(): Platform = Platform.Mobile
