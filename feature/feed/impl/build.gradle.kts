plugins {
    alias(libs.plugins.vk.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.feed.api)
            implementation(projects.auth.api)
            implementation(projects.data.vk.api)
            implementation(projects.core.ui)
        }
    }
}
