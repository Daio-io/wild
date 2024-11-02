package io.daio.gradle

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

fun Project.configureStaticAnalysis() {

    with(pluginManager) {
        apply("io.gitlab.arturbosch.detekt")
    }

    detekt {
        parallel = true

        source.setFrom(files(projectDir))

        config.setFrom(rootDir.resolve("detekt/config-compose.yml"))

        tasks.withType<Detekt>().configureEach {
            include("**/*.kt")
            exclude("**/resources", "**/build", "**/*.kts")
            reports {
                html.required.set(true)
                txt.required.set(true)
                xml.required.set(true)

                md.required.set(false)
                sarif.required.set(false)
            }
        }
    }

    dependencies {
        "detektPlugins"(libs.findLibrary("compose-rules-detekt").get())
    }
}

private fun Project.detekt(action: DetektExtension.() -> Unit) =
    extensions.configure<DetektExtension>(action)
