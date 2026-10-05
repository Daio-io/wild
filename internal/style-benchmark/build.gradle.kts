// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import io.daio.gradle.Versions

plugins {
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    // THE-511 exception to the ticket's exact Gradle snippet / "no further dependencies":
    // StyleDefaultsBenchmark uses Color.Red for customColors. :style and :components:button
    // expose Compose as implementation (not api), so Color is not on this consumer classpath
    // without an explicit androidTest-only Compose edge.
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
    // See THE-511 exception on id("io.daio.compose") above.
    androidTestImplementation(compose.ui)
    androidTestImplementation(compose.foundation)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit)
}
