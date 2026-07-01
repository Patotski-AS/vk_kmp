package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow

internal actual class PlatformAuthLauncher actual constructor(
    private val oauthFlow: VkOAuthFlow,
    private val credentials: VkAppCredentials,
) : AuthLauncher {
    private val stub = StubAuthLauncher()

    override suspend fun launch() =
        if (!credentials.isConfigured) {
            stub.launch()
        } else {
            oauthFlow.authorize()
        }
}
