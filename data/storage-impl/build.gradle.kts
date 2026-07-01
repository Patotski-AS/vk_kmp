plugins {
    alias(libs.plugins.vk.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.storageApi)
            implementation(projects.auth.api)
            implementation(libs.koin.core)
        }
    }
}
