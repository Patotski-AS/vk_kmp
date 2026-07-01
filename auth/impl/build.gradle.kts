plugins {
    alias(libs.plugins.vk.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.auth.api)
            implementation(projects.data.storage.api)
            implementation(libs.koin.core)
        }
    }
}
