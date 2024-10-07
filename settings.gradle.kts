rootProject.name = "wild"

pluginManagement {
    includeBuild("gradle/build-logic")

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":playbook:android",
    ":playbook:desktop",
    ":playbook:shared",
    ":playbook:web",
    ":playbook:androidTv",
)
include("foundations")
include("modifier")
include(":tv:components:button")
include(":tv:layout:container")
