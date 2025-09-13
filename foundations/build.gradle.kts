// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
}

android {
    namespace = "io.daio.wild.foundations"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(projects.modifier)
            }
        }
    }
}

metalava {
    filename.set("api/api.txt")
}
