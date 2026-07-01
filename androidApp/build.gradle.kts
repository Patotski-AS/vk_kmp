import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vkid.manifest.placeholders)
}

val localProperties = rootProject.file("local.properties")
val vkProperties = Properties().apply {
    if (localProperties.exists()) {
        load(localProperties.inputStream())
    }
}
val vkClientId = vkProperties.getProperty("vk.client.id", "0")
val vkClientSecret = vkProperties.getProperty("vk.client.secret", "stub")

android {
    namespace = "com.vk.kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.vk.kmp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        addManifestPlaceholders(
            mapOf(
                "VKIDClientID" to vkClientId,
                "VKIDClientSecret" to vkClientSecret,
                "VKIDRedirectHost" to "vk.ru",
                "VKIDRedirectScheme" to "vk$vkClientId",
            ),
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(projects.app)
    implementation(libs.androidx.activity.compose)
}
