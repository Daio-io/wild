// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.ui.input.key.Key

/**
 * [Key.keyCode]s that general map to action buttons on hardware controls like
 * Tv remotes, keyboards and game pads.
 */
internal val HardwareEnterKeys =
    longArrayOf(
        Key.DirectionCenter.keyCode,
        Key.Enter.keyCode,
        Key.NumPadEnter.keyCode,
        Key.ButtonA.keyCode,
    )
