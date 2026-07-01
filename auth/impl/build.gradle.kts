plugins {
    alias(libs.plugins.vk.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

import java.util.Properties

import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
            implementation(projects.auth.api)
            implementation(projects.data.storage.api)
            implementation(projects.core.network)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
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
    }
}
