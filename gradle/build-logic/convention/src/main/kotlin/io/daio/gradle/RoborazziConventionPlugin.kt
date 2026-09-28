package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.RoborazziExtension

class RoborazziConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("io.github.takahirom.roborazzi")
        configureRoborazzi()
    }
}

private fun Project.configureRoborazzi() {
    extensions.configure<RoborazziExtension> {
        outputDir.set(layout.projectDirectory.dir("screenshots"))
        @OptIn(ExperimentalRoborazziApi::class)
        separateOutputDirs.set(true)
    }
    pluginManager.withPlugin("com.android.application") { configureAndroidRoborazzi() }
    pluginManager.withPlugin("com.android.library") { configureAndroidRoborazzi() }
    tasks.withType<Test>().configureEach {
        systemProperty("robolectric.pixelCopyRenderMode", "hardware")
    }
}

private fun Project.configureAndroidRoborazzi() = android {
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
