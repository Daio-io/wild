plugins {
    id("io.daio.android.library")
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

android {
    namespace = "io.daio.wild.playbook.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api(projects.foundations)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.core.ktx)
                api(projects.tv.layout.container)
            }
        }

        val jsMain by getting

        val jvmMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
    }
}
