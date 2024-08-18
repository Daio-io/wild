plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
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
