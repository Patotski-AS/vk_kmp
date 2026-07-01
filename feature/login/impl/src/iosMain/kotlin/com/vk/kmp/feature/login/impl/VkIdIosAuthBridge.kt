package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkTokens
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSNotificationCenter

private const val AUTH_REQUEST_NOTIFICATION = "VkIdAuthRequested"

object VkIdIosAuthBridge {
    private var pendingAuth: CompletableDeferred<AuthLaunchResult>? = null

    fun requestAuth() {
        pendingAuth = CompletableDeferred()
        NSNotificationCenter.defaultCenter.postNotificationName(AUTH_REQUEST_NOTIFICATION, null)
    }

    suspend fun awaitResult(): AuthLaunchResult? = pendingAuth?.await()

    fun completeSuccess(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        expiresAtEpochSeconds: Long?,
        deviceId: String?,
    ) {
        pendingAuth?.complete(
            AuthLaunchResult.Success(
                VkTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId,
                    expiresAtEpochSeconds = expiresAtEpochSeconds,
                    deviceId = deviceId,
                ),
            ),
        )
        pendingAuth = null
    }

    fun completeCancelled() {
        pendingAuth?.complete(AuthLaunchResult.Cancelled)
        pendingAuth = null
    }

    fun completeError(message: String) {
        pendingAuth?.complete(AuthLaunchResult.Error(message))
        pendingAuth = null
    }
}

fun completeVkIdIosAuthSuccess(
    accessToken: String,
    refreshToken: String?,
    userId: Long,
    expiresAtEpochSeconds: Long?,
    deviceId: String?,
) {
    VkIdIosAuthBridge.completeSuccess(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId,
        expiresAtEpochSeconds = expiresAtEpochSeconds,
        deviceId = deviceId,
    )
}

fun completeVkIdIosAuthCancelled() {
    VkIdIosAuthBridge.completeCancelled()
}

fun completeVkIdIosAuthError(message: String) {
    VkIdIosAuthBridge.completeError(message)
}
