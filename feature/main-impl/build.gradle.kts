plugins {
    alias(libs.plugins.vk.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.mainApi)
            implementation(projects.feature.feedApi)
            implementation(projects.core.ui)
        }
    }
}
