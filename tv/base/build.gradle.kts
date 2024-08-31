plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish")
}

dependencies {
    implementation(compose.foundation)
    api(projects.foundations)
}

android {
    namespace = "io.daio.wild.tv.base"
}
