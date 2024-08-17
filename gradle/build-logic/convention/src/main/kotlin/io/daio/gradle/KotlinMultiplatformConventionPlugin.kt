package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            applyDefaultHierarchyTemplate()

            if (pluginManager.hasPlugin("com.android.library")) {
                androidTarget {
                    publishLibraryVariants("release")
                }
            }

            jvm()

            iosX64()
            iosArm64()
            iosSimulatorArm64()

            js {
                browser()
                binaries.executable()
            }

            configureSpotless()
            configureStaticAnalysis()
            configureKotlin()
        }
    }
}
