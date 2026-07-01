package com.vk.kmp.feature.login.impl

internal object VkIdCredentials {
    val clientId: String = BuildConfig.VK_CLIENT_ID
    val clientSecret: String = BuildConfig.VK_CLIENT_SECRET

    val isConfigured: Boolean
        get() = clientId.isNotBlank() &&
            clientId != "0" &&
            clientSecret.isNotBlank() &&
            clientSecret != "stub"
}
