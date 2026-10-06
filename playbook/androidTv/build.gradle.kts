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
    implementation(compose.foundation)
    implementation(projects.layout.container)
    implementation(projects.components.button)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.ui.test.junit4)
    testImplementation(projects.internal.screenshotTests)
    testImplementation(libs.roborazzi.android)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit)
    debugImplementation(libs.ui.test.manifest)
}

android {
    namespace = "io.daio.wild.playbook.tv"

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        applicationId = "io.daio.wild.playbook.tv"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        unitTests.all {
            it.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
            // Roborazzi convention filters UnitTest to *ScreenshotTest; keep scenario coverage enrolled.
            it.filter.isFailOnNoMatchingTests = false
            it.filter.includeTestsMatching("*TvBenchmarkScenarioTest")
            val configuredLocalRepository = System.getProperty("maven.repo.local")
            val resolvedLocalRepository =
                configuredLocalRepository
                    ?.takeUnless { path -> path.isBlank() || "\${user.home}" in path }
                    ?: "${System.getProperty("user.home")}/.m2/repository"
            it.systemProperty(
                "maven.repo.local",
                resolvedLocalRepository,
            )
        }
    }

    buildTypes {
        debug {
            isDebuggable = false
        }
        release {
            isDebuggable = false

            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["debug"]
            proguardFiles(
                "proguard-rules.pro",
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )
        }
    }
}
