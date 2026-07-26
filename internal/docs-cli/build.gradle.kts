// Copyright 2024, Dai Williams
// SPDX-License-Identifier: Apache-2.0
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("io.daio.root")
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    application
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

application {
    mainClass.set("io.daio.wild.docs.cli.MainKt")
}

dependencies {
    implementation(projects.components.button)
    implementation(compose.foundation)

    testImplementation(kotlin("test-junit"))
    testImplementation(libs.junit)
}

sourceSets {
    main {
        kotlin.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "../../components/button/src/commonMain/kotlin",
            ),
        )
        kotlin.include(
            "io/daio/wild/docs/**/*.kt",
            "**/*.doc.kt",
            "**/*.samples.kt",
        )
    }
}

tasks.withType<Test>().configureEach {
    systemProperty("wild.repoRoot", rootProject.projectDir.absolutePath)
}

tasks.withType<JavaExec>().configureEach {
    workingDir(rootProject.projectDir)
}
