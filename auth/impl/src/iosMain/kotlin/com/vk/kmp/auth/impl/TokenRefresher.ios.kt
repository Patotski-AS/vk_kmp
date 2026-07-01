package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkTokens

internal actual suspend fun platformRefresh(
    tokens: VkTokens,
    state: String,
    oauthRefresher: OAuthApiTokenRefresher,
): VkTokens = oauthRefresher.refresh(tokens, state)
