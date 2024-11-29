// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.daio.common.CustomDesignSystemApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        CustomDesignSystemApp()
    }
}
