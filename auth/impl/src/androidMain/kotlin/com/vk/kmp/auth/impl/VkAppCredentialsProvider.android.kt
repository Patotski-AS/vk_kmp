package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials

internal actual fun platformVkAppCredentials(): VkAppCredentials =
    StaticVkAppCredentials(
        id = BuildConfig.VK_CLIENT_ID,
        secret = BuildConfig.VK_CLIENT_SECRET,
    )
