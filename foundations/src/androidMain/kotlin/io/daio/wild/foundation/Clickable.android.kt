// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.foundation

import androidx.compose.ui.input.key.KeyEvent

actual val KeyEvent.repeatCount: Int
    get() = nativeKeyEvent.repeatCount
