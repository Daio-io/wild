// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import io.daio.gradle.Versions

plugins {
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.compose")
    alias(libs.plugins.androidx.benchmark)
}

android {
    namespace = "io.daio.wild.style.benchmark"
    testBuildType = "release"

    defaultConfig {
        minSdk = Versions.MIN_SDK
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.output.enable"] = "true"
    }
}

dependencies {
    androidTestImplementation(projects.style)
    androidTestImplementation(projects.components.button)
    androidTestImplementation(compose.ui)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit)
}
