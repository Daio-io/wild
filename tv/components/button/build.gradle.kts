plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish")
}

dependencies {
    implementation(compose.foundation)
    api(projects.foundations)
    api(projects.tv.layout.container)
}

android {
    namespace = "io.daio.wild.tv.components.button"
}
