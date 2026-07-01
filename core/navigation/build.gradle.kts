plugins {
    alias(libs.plugins.vk.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.decompose)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(projects.auth.api)
            implementation(projects.feature.login.api)
            implementation(projects.feature.main.api)
        }
    }
}
