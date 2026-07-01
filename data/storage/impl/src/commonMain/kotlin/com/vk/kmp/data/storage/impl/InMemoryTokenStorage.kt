package com.vk.kmp.data.storage.impl

import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.data.storage.api.TokenStorage

internal class InMemoryTokenStorage : TokenStorage {
    private var tokens: VkTokens? = null

    override fun getTokens(): VkTokens? = tokens

    override fun saveTokens(tokens: VkTokens) {
        this.tokens = tokens
    }

    override fun clear() {
        tokens = null
    }
}
