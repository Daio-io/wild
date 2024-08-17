plugins {
    id("io.daio.compose")
    id("io.daio.android.application")
    id("io.daio.kotlin.android")
}

dependencies {
    implementation(project(":playbook:shared"))
    implementation("androidx.activity:activity-compose:1.9.1")
}

android {
    namespace = "io.daio.wild"
}
