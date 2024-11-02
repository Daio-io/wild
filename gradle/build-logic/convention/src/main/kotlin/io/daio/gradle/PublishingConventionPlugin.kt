package io.daio.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.vanniktech.maven.publish")

        if (project.hasProperty("useGitHubPublishing")) {
            configureGitHubPublishing()
        }
    }
}
