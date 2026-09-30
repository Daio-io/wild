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
    sourceSets.commonMain.dependencies {
        api(projects.a2uiCompose)
        api(projects.components.text)
        api(projects.components.toggleable)
        api(projects.components.button)
        api(projects.components.icon)
        api(projects.components.progress)
        api(projects.layout.container)
        api(projects.layout.divider)
        implementation(compose.foundation)
    }
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        implementation(compose.uiTest)
    }
    sourceSets.jvmTest.dependencies { implementation(compose.desktop.currentOs) }
}
