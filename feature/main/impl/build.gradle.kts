plugins {
    alias(libs.plugins.vk.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.main.api)
            implementation(projects.feature.feed.api)
            implementation(projects.core.ui)
        }
    }
}
