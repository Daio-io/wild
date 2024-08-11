import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("io.daio.compose")
    id("io.daio.kotlin.multiplatform")
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":playbook:shared"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Wild Demo"
            packageVersion = "1.0.0"
        }
    }
}
