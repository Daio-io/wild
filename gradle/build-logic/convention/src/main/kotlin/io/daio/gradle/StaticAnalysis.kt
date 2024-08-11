package io.daio.gradle

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register

fun Project.configureStaticAnalysis() {

    with(pluginManager) {
        apply("io.gitlab.arturbosch.detekt")
    }

    tasks.register<Detekt>("composeDetekt") {
        description = "Runs detekt for Compose code."

        parallel = true

        setSource(files(projectDir))

        config.setFrom(rootDir.resolve("detekt/config-compose.yml"))

        // Disable everything but compose-rules
        disableDefaultRuleSets = true

        reports {
            html.required.set(true)
            txt.required.set(true)
            xml.required.set(true)

            md.required.set(false)
            sarif.required.set(false)
        }

        // No kts for now as we are only using the compose rules.
        include("**/*.kt")
        exclude("**/resources", "**/build")
    }

    dependencies {
        "detektPlugins"(libs.findLibrary("compose-rules-detekt").get())
    }
}
