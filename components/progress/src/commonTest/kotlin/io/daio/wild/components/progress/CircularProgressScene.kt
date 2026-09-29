// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.daio.wild.screenshot.ScreenshotSurface

@Composable
internal fun CircularProgressScene() {
    ScreenshotSurface {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularProgressIndicator(progress = { 0f })
            CircularProgressIndicator(progress = { .5f })
            CircularProgressIndicator(progress = { 1f })
            CircularProgressIndicator()
        }
    }
}
