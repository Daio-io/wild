package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        configureCompose()
    }
}

internal fun Project.configureCompose() {
    extensions.configure<ComposeCompilerGradlePluginExtension>("composeCompiler") {
        includeSourceInformation.set(true)
        if (providers.gradleProperty("composeReports").orNull == "true") {
            val destination = layout.buildDirectory.dir("compose_compiler")
            reportsDestination.set(destination)
            metricsDestination.set(destination)
        }
    }
}
