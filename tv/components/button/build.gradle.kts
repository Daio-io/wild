plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish.github")
}

dependencies {
    implementation(compose.foundation)
    api(projects.tv.foundation)
}

android {
    namespace = "io.daio.wild.tv.components.button"
}
