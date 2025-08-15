// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import io.daio.gradle.Versions

plugins {
    id("io.daio.android.test")
    id("io.daio.kotlin.android")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "io.daio.wild.benchmark"

    defaultConfig {
        minSdk = Versions.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    targetProjectPath = ":playbook:androidTv"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.uiautomator)
}
