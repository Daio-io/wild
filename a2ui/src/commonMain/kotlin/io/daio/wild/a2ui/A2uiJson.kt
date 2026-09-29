// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.a2ui

import kotlinx.serialization.json.Json

internal val jsonParser =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
