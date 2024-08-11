package io.daio.gradle

import com.android.build.gradle.internal.crash.afterEvaluate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.repositories

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.gradle.android.cache-fix")
            }

            configureAndroid()
        }
    }
}
