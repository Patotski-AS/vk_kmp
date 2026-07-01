package com.vk.kmp.data.storage.api

import com.vk.kmp.auth.api.VkTokens

interface TokenStorage {
    fun getTokens(): VkTokens?
    fun saveTokens(tokens: VkTokens)
    fun clear()
}
