package io.daio.gradle

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

internal fun Project.configureGitHubPublishing() {
    val gitHubPackagesUsername: String? by project
    val gitHubPackagesPassword: String? by project
    val gitHubPackageUri: String? by project
    val gitCommit: String? by project

    val user =
        gitHubPackagesUsername ?: project.findProperty("gitHubPackagesUsername").toString()
    val pass = gitHubPackagesPassword ?: project.findProperty("gitHubPackagesPassword").toString()
    val commit = gitCommit ?: project.findProperty("gitCommit").toString()
    val versionName = project.findProperty("VERSION_NAME").toString()

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/daio-io/wild")
                version = "${versionName}-$commit"
                credentials {
                    username = user
                    password = pass
                }
            }
        }
    }
}

private fun Project.publishing(action: PublishingExtension.() -> Unit) =
    extensions.configure<PublishingExtension>(action)