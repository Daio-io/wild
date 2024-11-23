// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.android.library")
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

android {
    namespace = "io.daio.wild.playbook.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api(projects.foundations)
                api(projects.contentColor)
                api(projects.style)
                api(compose.material3)
                api(projects.style)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.core.ktx)
                // Should not really expose at this level but its just for samples.
                api(libs.tv.material)
                api(projects.layout.container)
            }
        }

        val jsMain by getting

        val jvmMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
    }
}
