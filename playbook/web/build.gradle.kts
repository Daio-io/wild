// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    sourceSets {
        commonMain.dependencies {
            implementation(projects.playbook.shared)
            implementation(compose.components.resources)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
        }
    }
}
