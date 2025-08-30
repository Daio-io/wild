// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.content

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.graphics.Color

internal actual val LocalAlternatePlatformColor: ProvidableCompositionLocal<Color> =
    try {
        androidx.tv.material3.LocalContentColor
    } catch (_: Throwable) {
        LocalContentColor
    }
