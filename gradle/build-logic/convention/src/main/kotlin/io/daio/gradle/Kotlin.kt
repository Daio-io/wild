package io.daio.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

fun Project.configureKotlin(setAllWarningsAsErrors: Boolean = false) {
    // Configure Java to use our chosen language level. Kotlin will automatically pick this up
    configureJava()

    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            // Treat all Kotlin warnings as errors
            if (setAllWarningsAsErrors) {
                allWarningsAsErrors.set(true)
            }

            if (this is KotlinJvmCompilerOptions) {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }
}
