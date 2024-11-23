// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.runtime.CompositionLocalAccessorScope

internal actual fun CompositionLocalAccessorScope.getPlatformInteractions(): PlatformInteractions =
    PlatformInteractions(requiresHardwareInput = false)
