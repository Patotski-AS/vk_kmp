package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow

internal actual class PlatformAuthLauncher actual constructor(
    oauthFlow: VkOAuthFlow,
    private val credentials: VkAppCredentials,
) : AuthLauncher {
    private val stub = StubAuthLauncher()

    override suspend fun launch(): AuthLaunchResult {
        if (!credentials.isConfigured) {
            return stub.launch()
        }

        VkIdIosAuthBridge.requestAuth()
        return VkIdIosAuthBridge.awaitResult() ?: AuthLaunchResult.Error(
            "VK ID iOS auth was not completed",
        )
    }
}
