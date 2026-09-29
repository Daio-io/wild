// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.components.button

import androidx.compose.foundation.text.BasicText
import io.daio.wild.docs.model.componentSample

internal val BasicButtonSample =
    componentSample(
        id = "button-basic",
        sourceFile = "Button.samples.kt",
        symbol = "BasicButtonSample",
    ) {
        Button(onClick = {}) {
            BasicText("Continue")
        }
    }
