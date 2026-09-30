// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun LinearProgressEmpty() {
    ScreenshotSurface {
        LinearProgressIndicator(progress = { 0f }, Modifier.fillMaxWidth())
    }
}

@Composable
internal fun LinearProgressHalf() {
    ScreenshotSurface {
        LinearProgressIndicator(progress = { .5f }, Modifier.fillMaxWidth())
    }
}

@Composable
internal fun LinearProgressFull() {
    ScreenshotSurface {
        LinearProgressIndicator(progress = { 1f }, Modifier.fillMaxWidth())
    }
}

@Composable
internal fun LinearProgressIndeterminate() {
    ScreenshotSurface {
        LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

@Composable
internal fun CircularProgressEmpty() {
    ScreenshotSurface {
        CircularProgressIndicator(progress = { 0f })
    }
}

@Composable
internal fun CircularProgressHalf() {
    ScreenshotSurface {
        CircularProgressIndicator(progress = { .5f })
    }
}

@Composable
internal fun CircularProgressFull() {
    ScreenshotSurface {
        CircularProgressIndicator(progress = { 1f })
    }
}

@Composable
internal fun CircularProgressIndeterminate() {
    ScreenshotSurface {
        CircularProgressIndicator()
    }
}
