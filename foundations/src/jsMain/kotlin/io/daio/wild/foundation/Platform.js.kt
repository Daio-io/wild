package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope

internal actual fun CompositionLocalAccessorScope.getPlatform(): Platform = Platform.Web
