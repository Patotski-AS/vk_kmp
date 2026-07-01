plugins {
    alias(libs.plugins.vk.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.loginApi)
            implementation(projects.auth.api)
            implementation(projects.core.ui)
        }
    }
}
