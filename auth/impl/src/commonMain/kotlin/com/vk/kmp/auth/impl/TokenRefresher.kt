package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.core.network.applyVkDefaults
import com.vk.kmp.core.network.createHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.datetime.Clock

internal class TokenRefresher(
    private val credentials: VkAppCredentials,
) {
    private val httpClient = createHttpClient {
        applyVkDefaults()
    }

    suspend fun refresh(tokens: VkTokens, state: String): VkTokens {
        val refreshToken = tokens.refreshToken
            ?: throw TokenRefreshException("Refresh token is missing")
        val deviceId = tokens.deviceId
            ?: throw TokenRefreshException("Device id is missing")

        val parameters = Parameters.build {
            append("grant_type", "refresh_token")
            append("refresh_token", refreshToken)
            append("client_id", credentials.clientId)
            append("device_id", deviceId)
            append("state", state)
            if (credentials.isConfigured) {
                append("client_secret", credentials.clientSecret)
            }
        }

        val response = runCatching {
            httpClient.post(OAuthConstants.TOKEN_URL) {
                setBody(FormDataContent(parameters))
            }.body<VkTokenResponse>()
        }.getOrElse { throwable ->
            throw TokenRefreshException(throwable.message ?: "Token refresh failed", throwable)
        }

        if (response.state != null && response.state != state) {
            throw TokenRefreshException("OAuth state mismatch")
        }

        val expiresAt = response.expiresIn?.let { expiresIn ->
            Clock.System.now().epochSeconds + expiresIn
        }

        return VkTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken ?: refreshToken,
            userId = response.userId,
            expiresAtEpochSeconds = expiresAt,
            deviceId = deviceId,
        )
    }
}

internal class TokenRefreshException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
