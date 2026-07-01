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
            implementation(projects.data.storage.impl)
            implementation(projects.data.vk.impl)
            implementation(projects.feature.login.api)
            implementation(projects.feature.login.impl)
            implementation(projects.feature.main.api)
            implementation(projects.feature.main.impl)
            implementation(projects.feature.feed.api)
            implementation(projects.feature.feed.impl)

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
