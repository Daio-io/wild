package io.daio.gradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.gradle.android.cache-fix")
            }

            configureAndroid()

            extensions.configure<BaseExtension> {
                compileOptions {
                    // https://developer.android.com/studio/write/java8-support
                    isCoreLibraryDesugaringEnabled = true
                }
            }

            dependencies {
                // https://developer.android.com/studio/write/java8-support
                "coreLibraryDesugaring"(libs.findLibrary("tools.desugarjdklibs").get())
            }
        }
    }
}
