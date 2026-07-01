package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow
import com.vk.kmp.auth.api.VkTokens
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.VKIDAuthCallback
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal actual class PlatformAuthLauncher actual constructor(
  oauthFlow: VkOAuthFlow,
  credentials: VkAppCredentials,
) : AuthLauncher {
    private val stub = StubAuthLauncher()

    override suspend fun launch(): AuthLaunchResult = coroutineScope {
        if (!VkIdCredentials.isConfigured) {
            return@coroutineScope stub.launch()
        }

        suspendCancellableCoroutine { continuation ->
            var deviceId: String? = null
            val callback = object : VKIDAuthCallback {
                override fun onAuth(accessToken: AccessToken) {
                    if (continuation.isActive) {
                        continuation.resume(
                            AuthLaunchResult.Success(accessToken.toVkTokens(deviceId)),
                        )
                    }
                }

                override fun onAuthCode(authCodeData: AuthCodeData, isCompletion: Boolean) {
                    if (authCodeData.deviceId.isNotBlank()) {
                        deviceId = authCodeData.deviceId
                    }
                }

                override fun onFail(fail: VKIDAuthFail) {
                    if (!continuation.isActive) return
                    continuation.resume(
                        when (fail) {
                            is VKIDAuthFail.Canceled -> AuthLaunchResult.Cancelled
                            else -> AuthLaunchResult.Error(fail.description)
                        },
                    )
                }
            }

            launch {
                VKID.instance.authorize(callback)
            }
        }
    }

    private fun AccessToken.toVkTokens(deviceId: String?): VkTokens = VkTokens(
        accessToken = token,
        refreshToken = null,
        userId = userID,
        expiresAtEpochSeconds = expireTime.takeIf { it > 0 },
        deviceId = deviceId?.takeIf { it.isNotBlank() },
    )
}
