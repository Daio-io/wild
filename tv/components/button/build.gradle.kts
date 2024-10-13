plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish")
}

dependencies {
    implementation(compose.foundation)
    implementation(projects.tv.layout.container)

    api(projects.foundations)
    api(projects.style)
    api(projects.contentColor)
}

android {
    namespace = "io.daio.wild.tv.components.button"
}
