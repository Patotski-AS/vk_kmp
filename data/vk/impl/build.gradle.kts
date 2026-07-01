plugins {
    alias(libs.plugins.vk.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.vk.api)
            implementation(projects.auth.api)
            implementation(projects.core.network)
            implementation(libs.koin.core)
        }
    }
}
