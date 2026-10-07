// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.test.roborazzi")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
}

android {
    namespace = "io.daio.wild.components.progress"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.animation)
                api(projects.contentColor)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
                implementation(projects.internal.screenshotTests)
            }
        }
        val jvmTest by getting {
            dependencies {
                // Dejavu 0.5.0 publishes JVM/Android/iOS/wasmJs, not JS — keep off commonTest.
                implementation(libs.dejavu)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

metalava {
    filename.set("api/api.txt")
}
