package com.vk.kmp.data.storage.impl

import com.vk.kmp.auth.api.VkTokens
import kotlinx.serialization.Serializable

@Serializable
internal data class StoredTokens(
    val accessToken: String,
    val refreshToken: String?,
    val userId: Long,
    val expiresAtEpochSeconds: Long?,
) {
    fun toVkTokens(): VkTokens = VkTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId,
        expiresAtEpochSeconds = expiresAtEpochSeconds,
    )

    companion object {
        fun from(tokens: VkTokens): StoredTokens = StoredTokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            userId = tokens.userId,
            expiresAtEpochSeconds = tokens.expiresAtEpochSeconds,
        )
    }
}
