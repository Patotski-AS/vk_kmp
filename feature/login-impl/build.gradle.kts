import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.vk.cmp.feature)
    alias(libs.plugins.vkid.manifest.placeholders)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.loginApi)
            implementation(projects.auth.api)
            implementation(projects.core.ui)
        }
        androidMain.dependencies {
            implementation(libs.vkid)
        }
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val localProperties = rootProject.file("local.properties")
        val properties = Properties()
        if (localProperties.exists()) {
            properties.load(localProperties.inputStream())
        }

        val vkClientId = properties.getProperty("vk.client.id", "0")
        val vkClientSecret = properties.getProperty("vk.client.secret", "stub")

        buildConfigField("String", "VK_CLIENT_ID", "\"$vkClientId\"")
        buildConfigField("String", "VK_CLIENT_SECRET", "\"$vkClientSecret\"")

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to vkClientId,
                "VKIDClientSecret" to vkClientSecret,
                "VKIDRedirectHost" to "vk.ru",
                "VKIDRedirectScheme" to "vk$vkClientId",
            ),
        )
    }
}
