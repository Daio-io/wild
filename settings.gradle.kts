// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
rootProject.name = "wild"

pluginManagement {
    includeBuild("gradle/build-logic")

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Lib
include(
    "foundations",
    "content-color",
    "style",
    "modifier",
    ":components:button",
    ":layout:container",
)

// Test
include(
    ":internal:benchmark",
)

// Playbook
include(
    ":playbook:android",
    ":playbook:desktop",
    ":playbook:shared",
    ":playbook:web",
    ":playbook:androidTv",
)
