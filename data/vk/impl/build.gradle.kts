plugins {
    alias(libs.plugins.vk.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.vk.api)
            implementation(projects.auth.api)
            implementation(projects.core.network)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
