plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.android")
    id("io.daio.publish.github")
}

dependencies {
    implementation(compose.foundation)
}

android {
    namespace = "io.daio.wild.tv.foundation"
}
