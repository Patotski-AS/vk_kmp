import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.vkid.manifest.placeholders) apply true
}

val localProperties = file("local.properties")
val vkProperties = Properties().apply {
    if (localProperties.exists()) {
        load(localProperties.inputStream())
    }
}
val vkClientId = vkProperties.getProperty("vk.client.id", "0")
val vkClientSecret = vkProperties.getProperty("vk.client.secret", "stub")

vkidManifestPlaceholders {
    init(
        clientId = vkClientId,
        clientSecret = vkClientSecret,
    )
}
