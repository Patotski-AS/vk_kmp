package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkOAuthFlow

internal class UnsupportedVkOAuthFlow : VkOAuthFlow {
    override suspend fun authorize(): AuthLaunchResult =
        AuthLaunchResult.Error("OAuth is not supported on this platform")
}
