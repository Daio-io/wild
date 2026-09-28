// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.android.library")
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.test.roborazzi")
}

android {
    namespace = "io.daio.wild.screenshot"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(projects.contentColor)
            implementation(projects.layout.container)
            implementation(projects.components.text)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.roborazzi.core)
            implementation(libs.roborazzi.compose.desktop)
        }
        iosMain.dependencies {
            implementation(libs.roborazzi.compose.ios)
        }
        androidMain.dependencies {
            implementation(libs.junit)
            implementation(libs.activity.compose)
            implementation(libs.ui.test.junit4)
            implementation(libs.ui.test.manifest)
            implementation(libs.robolectric)
            implementation(libs.roborazzi.core)
            implementation(libs.roborazzi.android)
            implementation(libs.roborazzi.compose)
            implementation(libs.roborazzi.junit)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
