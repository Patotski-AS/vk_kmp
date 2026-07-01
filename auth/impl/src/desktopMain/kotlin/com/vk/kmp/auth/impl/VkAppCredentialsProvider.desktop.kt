package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties

internal actual fun platformVkAppCredentials(): VkAppCredentials {
    val fromEnv = StaticVkAppCredentials(
        id = System.getenv("VK_CLIENT_ID").orEmpty(),
        secret = System.getenv("VK_CLIENT_SECRET").orEmpty(),
    )
    if (fromEnv.isConfigured) return fromEnv

    val configFile = Path.of(System.getProperty("user.home"), ".vk_kmp", "credentials.properties")
    if (Files.exists(configFile)) {
        val properties = Properties().apply {
            Files.newInputStream(configFile).use(::load)
        }
        val fromFile = StaticVkAppCredentials(
            id = properties.getProperty("vk.client.id", ""),
            secret = properties.getProperty("vk.client.secret", ""),
        )
        if (fromFile.isConfigured) return fromFile
    }

    return StaticVkAppCredentials(id = "0", secret = "stub")
}
