package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials

internal class StaticVkAppCredentials(
    private val id: String,
    private val secret: String,
) : VkAppCredentials {
    override val clientId: String = id
    override val clientSecret: String = secret
    override val isConfigured: Boolean
        get() = id.isNotBlank() &&
            id != "0" &&
            id != "YOUR_APP_ID" &&
            secret.isNotBlank() &&
            secret != "stub"
}

internal expect fun platformVkAppCredentials(): VkAppCredentials
