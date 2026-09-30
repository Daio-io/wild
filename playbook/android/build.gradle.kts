// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
    id("io.daio.test.roborazzi")
}

dependencies {
    implementation(projects.playbook.shared)

    testImplementation(projects.internal.screenshotTests)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
}

android {
    namespace = "io.daio.wild.playbook.android"
}
