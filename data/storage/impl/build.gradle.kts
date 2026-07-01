plugins {
    alias(libs.plugins.vk.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.storage.api)
            implementation(projects.auth.api)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}
