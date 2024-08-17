plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(projects.playbook.shared)
}

android {
    namespace = "io.daio.wild.playbook.android"
}
