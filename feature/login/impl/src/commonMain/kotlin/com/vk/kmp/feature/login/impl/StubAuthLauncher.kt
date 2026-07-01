package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkTokens

internal class StubAuthLauncher : AuthLauncher {
    override suspend fun launch(): AuthLaunchResult =
        AuthLaunchResult.Success(
            VkTokens(
                accessToken = "stub-token",
                refreshToken = null,
                userId = 1L,
                expiresAtEpochSeconds = null,
            ),
        )
}
