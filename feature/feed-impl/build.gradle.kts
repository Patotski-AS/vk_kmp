plugins {
    alias(libs.plugins.vk.cmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.feedApi)
            implementation(projects.auth.api)
            implementation(projects.data.vkApi)
            implementation(projects.core.ui)
        }
    }
}
