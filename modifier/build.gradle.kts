plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
    id("io.daio.publish")
    alias(libs.plugins.dokka)
    alias(libs.plugins.metalava)
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

metalava {
    filename.set("api/api.txt")
}
