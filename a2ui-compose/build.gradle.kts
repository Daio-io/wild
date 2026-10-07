// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
}

metalava { filename.set("api/api.txt") }

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.a2ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        jvmTest.dependencies {
            // Dejavu 0.5.0 publishes JVM/Android/iOS/wasmJs, not JS — keep off commonTest.
            implementation(libs.dejavu)
            implementation(compose.desktop.currentOs)
        }
    }
}
