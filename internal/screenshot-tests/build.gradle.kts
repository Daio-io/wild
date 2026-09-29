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
            api(compose.foundation)
            implementation(projects.foundations)
            implementation(projects.contentColor)
            implementation(projects.style)
            implementation(projects.modifier)
            api(projects.layout.container)
            implementation(projects.layout.divider)
            implementation(projects.components.button)
            implementation(projects.components.icon)
            implementation(projects.components.text)
            implementation(projects.components.toggleable)
            implementation(projects.components.listItem)
            implementation(projects.components.progress)
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
