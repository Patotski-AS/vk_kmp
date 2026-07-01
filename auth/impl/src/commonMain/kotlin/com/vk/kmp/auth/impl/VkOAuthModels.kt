package com.vk.kmp.auth.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class VkTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresIn: Long? = null,
    @SerialName("user_id") val userId: Long,
    val state: String? = null,
)

@Serializable
internal data class VkOAuthErrorResponse(
    val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null,
)
