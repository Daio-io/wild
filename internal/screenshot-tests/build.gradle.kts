// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.android.library")
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

android {
    namespace = "io.daio.wild.screenshot"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(compose.foundation)
        }
        jvmMain.dependencies {
            api(compose.desktop.currentOs)
            api(libs.roborazzi.core)
            api(libs.roborazzi.compose.desktop)
        }
        iosMain.dependencies {
            api(libs.roborazzi.compose.ios)
        }
        androidMain.dependencies {
            api(libs.junit)
            api(libs.activity.compose)
            api(libs.ui.test.junit4)
            api(libs.ui.test.manifest)
            api(libs.robolectric)
            api(libs.roborazzi.core)
            api(libs.roborazzi.android)
            api(libs.roborazzi.compose)
            api(libs.roborazzi.junit)
        }
    }
}
