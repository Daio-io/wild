// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.root")

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.cacheFixPlugin) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.metalava) apply false
    // Applied to use ./gradlew dokkaHtmlMultiModule
    alias(libs.plugins.dokka)
}
