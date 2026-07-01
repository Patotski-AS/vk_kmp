plugins {
    alias(libs.plugins.vk.kmp.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.decompose)
            implementation(projects.feature.feedApi)
        }
    }
}
