plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.roborazzi.gradlePlugin)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "io.daio.kotlin.multiplatform"
            implementationClass = "io.daio.gradle.KotlinMultiplatformConventionPlugin"
        }

        register("Publishing") {
            id = "io.daio.publish"
            implementationClass = "io.daio.gradle.PublishingConventionPlugin"
        }

        register("GitHubPublish") {
            id = "io.daio.publish.github"
            implementationClass = "io.daio.gradle.GitHubPackagingConventionPlugin"
        }

        register("root") {
            id = "io.daio.root"
            implementationClass = "io.daio.gradle.RootConventionPlugin"
        }

        register("kotlinAndroid") {
            id = "io.daio.kotlin.android"
            implementationClass = "io.daio.gradle.KotlinAndroidConventionPlugin"
        }

        register("androidApplication") {
            id = "io.daio.android.application"
            implementationClass = "io.daio.gradle.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "io.daio.android.library"
            implementationClass = "io.daio.gradle.AndroidLibraryConventionPlugin"
        }

        register("androidTest") {
            id = "io.daio.android.test"
            implementationClass = "io.daio.gradle.AndroidTestConventionPlugin"
        }

        register("compose") {
            id = "io.daio.compose"
            implementationClass = "io.daio.gradle.ComposeMultiplatformConventionPlugin"
        }

        register("screenshot") {
            id = "io.daio.test.roborazzi"
            implementationClass = "io.daio.gradle.RoborazziConventionPlugin"
        }
    }
}
