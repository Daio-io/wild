package io.daio.gradle

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

internal fun Project.configureGitHubPublishing() {
    val githubUser: String? by project
    val githubToken: String? by project
    val gitHubPackageUri: String? by project
    val gitCommit: String? by project

    val user = githubUser ?: project.findProperty("githubUser").toString()
    val pass = githubToken ?: project.findProperty("githubToken").toString()

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/daio-io/wild")
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