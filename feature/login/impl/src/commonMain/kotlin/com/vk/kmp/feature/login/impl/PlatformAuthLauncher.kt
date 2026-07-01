package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow

internal expect class PlatformAuthLauncher(
    oauthFlow: VkOAuthFlow,
    credentials: VkAppCredentials,
) : AuthLauncher
