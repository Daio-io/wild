package io.daio.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.configureSpotless() {
    val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion

    with(pluginManager) {
        apply("com.diffplug.spotless")
    }

    spotless {
        // Workaround for https://github.com/diffplug/spotless/issues/1644
        lineEndings = LineEnding.PLATFORM_NATIVE

        kotlin {
            target("src/**/*.kt")
            targetExclude("**/generated/**")
            ktlint(ktlintVersion)
            licenseHeaderFile(rootProject.file("config/copyright.txt"))
        }

        kotlinGradle {
            target("*.kts")
            ktlint(ktlintVersion)
            licenseHeaderFile(rootProject.file("config/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
        }
    }
}

private fun Project.spotless(action: SpotlessExtension.() -> Unit) = extensions.configure<SpotlessExtension>(action)
