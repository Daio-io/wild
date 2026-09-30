// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
plugins {
    id("io.daio.root")

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.cacheFixPlugin) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.metalava) apply false
    // Applied to use ./gradlew dokkaGenerate
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(projects.foundations)
    dokka(projects.contentColor)
    dokka(projects.style)
    dokka(projects.modifier)
    dokka(projects.components.button)
    dokka(projects.components.icon)
    dokka(projects.components.text)
    dokka(projects.components.toggleable)
    dokka(projects.components.listItem)
    dokka(projects.components.progress)
    dokka(projects.components.slider)
    dokka(projects.layout.container)
    dokka(projects.layout.divider)
}

tasks.register("verifyHostScreenshots") {
    group = "verification"
    description = "Verifies Android, Desktop, and iOS Roborazzi screenshots."
    dependsOn(
        ":internal:screenshot-tests:verifyRoborazziDebug",
        ":internal:screenshot-tests:verifyRoborazziJvm",
        // Library iOS goldens are not committed yet (Phase 2a recorded Android/Desktop only).
        ":internal:screenshot-tests:verifyRoborazziIosSimulatorArm64",
        ":components:button:verifyRoborazziDebug",
        ":components:button:verifyRoborazziJvm",
        ":components:icon:verifyRoborazziDebug",
        ":components:icon:verifyRoborazziJvm",
        ":components:list-item:verifyRoborazziDebug",
        ":components:list-item:verifyRoborazziJvm",
        ":components:progress:verifyRoborazziDebug",
        ":components:progress:verifyRoborazziJvm",
        ":components:text:verifyRoborazziDebug",
        ":components:text:verifyRoborazziJvm",
        ":components:toggleable:verifyRoborazziDebug",
        ":components:toggleable:verifyRoborazziJvm",
        ":content-color:verifyRoborazziDebug",
        ":content-color:verifyRoborazziJvm",
        ":layout:container:verifyRoborazziDebug",
        ":layout:container:verifyRoborazziJvm",
        ":layout:divider:verifyRoborazziDebug",
        ":layout:divider:verifyRoborazziJvm",
        ":playbook:android:verifyRoborazziDebug",
        ":playbook:androidTv:verifyRoborazziDebug",
        ":playbook:desktop:verifyRoborazziJvm",
    )
}
