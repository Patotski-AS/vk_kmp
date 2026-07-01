pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-android/")
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/maven/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-android/")
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/maven/")
        maven(url = "https://artifactory-external.vkpartner.ru/artifactory/vk-id-captcha/android/")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "vk_kmp"

include(":app")
include(":androidApp")
include(":desktopApp")

include(":core:common")
include(":core:ui")
include(":core:navigation")
include(":core:network")

include(":auth:api")
include(":auth:impl")

include(":data:storage-api")
include(":data:storage-impl")
include(":data:vk-api")
include(":data:vk-impl")

include(":feature:login-api")
include(":feature:login-impl")
include(":feature:main-api")
include(":feature:main-impl")
include(":feature:feed-api")
include(":feature:feed-impl")
