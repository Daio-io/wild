// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
package io.daio.wild.site.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class ComponentPageData(
    val name: String,
    val description: String,
    val module: String,
    val demos: List<Demo>,
    val usage: String,
    val props: List<Prop>,
    val platforms: List<Platform>,
)

@Immutable
data class Demo(
    val title: String,
    val description: String,
    val content: @Composable () -> Unit,
)

data class Prop(
    val name: String,
    val type: String,
    val default: String? = null,
    val required: Boolean = false,
)

enum class Platform(val label: String) {
    Android("Android"),
    AndroidTV("Android TV"),
    Desktop("Desktop"),
    MacOS("macOS"),
    Web("Web"),
    IOS("iOS"),
}
