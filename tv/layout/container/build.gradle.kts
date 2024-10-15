plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(compose.foundation)
    implementation(projects.modifier)

    api(projects.style)
    api(projects.contentColor)
    api(projects.foundations)
}

android {
    namespace = "io.daio.wild.tv.layout.container"
}
