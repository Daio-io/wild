plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
}

android {
    namespace = "io.daio.wild.container"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(projects.modifier)

                api(projects.style)
                api(projects.contentColor)
                api(projects.foundations)
            }
        }
    }
}
