plugins {
    id("io.daio.compose")
    id("io.daio.android.library")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
}

android {
    namespace = "io.daio.wild.foundations"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
            }
        }
    }
}
