// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(projects.layout.container)

                api(projects.foundations)
                api(projects.style)
                api(projects.contentColor)
            }
        }
    }
}

metalava {
    filename.set("api/api.txt")
}
