package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate

class GitHubPackagingConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val githubUser: String = project.findProperty("githubUser") as? String ?: "No User"
        val githubToken: String = project.findProperty("githubToken") as? String ?: "No Token"
        val githubPackage: String = project.findProperty("githubPackage") as? String ?: "No Package"

        pluginManager.apply("com.vanniktech.maven.publish")

        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri(githubPackage)
                    credentials {
                        username = githubUser
                        password = githubToken
                    }
                }
            }
        }
    }
}

private fun Project.publishing(action: PublishingExtension.() -> Unit) =
    extensions.configure<PublishingExtension>(action)