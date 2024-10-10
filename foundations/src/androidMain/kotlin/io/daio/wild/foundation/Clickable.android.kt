package io.daio.wild.foundation

import androidx.compose.ui.input.key.KeyEvent

internal actual val KeyEvent.repeatCount: Int
    get() = nativeKeyEvent.repeatCount
