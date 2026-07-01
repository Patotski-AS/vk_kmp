plugins {
    alias(libs.plugins.vk.kmp.api)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
        }
    }
}
