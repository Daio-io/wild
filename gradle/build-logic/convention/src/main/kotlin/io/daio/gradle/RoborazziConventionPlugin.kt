package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RoborazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("io.github.takahirom.roborazzi")
        configureRoborazzi()
    }
}

private fun Project.configureRoborazzi() {
    android {
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
                isReturnDefaultValues = true
            }
        }
    }
}
