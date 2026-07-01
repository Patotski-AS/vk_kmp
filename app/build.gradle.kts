plugins {
    alias(libs.plugins.vk.app)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.ui)
            implementation(projects.core.navigation)
            implementation(projects.auth.api)
            implementation(projects.auth.impl)
            implementation(projects.data.storageImpl)
            implementation(projects.data.vkImpl)
            implementation(projects.feature.loginApi)
            implementation(projects.feature.loginImpl)
            implementation(projects.feature.mainApi)
            implementation(projects.feature.mainImpl)
            implementation(projects.feature.feedApi)
            implementation(projects.feature.feedImpl)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}
