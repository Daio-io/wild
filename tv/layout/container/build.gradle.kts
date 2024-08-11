plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish")
}

dependencies {
    implementation(compose.foundation)
    api(projects.tv.base)
}

android {
    namespace = "io.daio.wild.tv.layout.container"
}
