// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.android.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TvLayout()
        }
    }
}
