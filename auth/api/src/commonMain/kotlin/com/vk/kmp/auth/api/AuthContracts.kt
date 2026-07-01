package com.vk.kmp.auth.api

import kotlinx.coroutines.flow.StateFlow

data class VkTokens(
    val accessToken: String,
    val refreshToken: String?,
    val userId: Long,
    val expiresAtEpochSeconds: Long?,
    val deviceId: String? = null,
)

interface VkAppCredentials {
    val clientId: String
    val clientSecret: String
    val isConfigured: Boolean
}

interface VkOAuthFlow {
    suspend fun authorize(): AuthLaunchResult
}

sealed interface SessionState {
    data object Unauthenticated : SessionState
    data object Refreshing : SessionState
    data class Authenticated(
        val userId: Long,
        val expiresAtEpochSeconds: Long?,
    ) : SessionState
}

enum class AuthZone {
    Unauthorized,
    Authorized,
}

interface SessionRepository {
    val sessionState: StateFlow<SessionState>
    suspend fun getValidAccessToken(): String
    suspend fun refreshAccessToken(): String
    suspend fun logout()
}

interface AuthZoneController {
    val zone: StateFlow<AuthZone>
}

interface AuthResultHandler {
    suspend fun onLoginSuccess(tokens: VkTokens)
}

interface AuthLauncher {
    suspend fun launch(): AuthLaunchResult
}

sealed interface AuthLaunchResult {
    data class Success(val tokens: VkTokens) : AuthLaunchResult
    data object Cancelled : AuthLaunchResult
    data class Error(val message: String) : AuthLaunchResult
}
