// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(projects.playbook.shared)
    implementation(compose.foundation)
    implementation(projects.layout.container)
    implementation(projects.components.button)
}

android {
    namespace = "io.daio.wild.playbook.tv"

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        applicationId = "io.daio.wild.playbook.tv"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    buildTypes {
        debug {
            isDebuggable = false
        }
        release {
            isDebuggable = false

            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["debug"]
            proguardFiles(
                "proguard-rules.pro",
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
    }
}
