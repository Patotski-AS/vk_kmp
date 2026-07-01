package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkTokens
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.refresh.VKIDRefreshTokenCallback
import com.vk.id.refresh.VKIDRefreshTokenFail
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual suspend fun platformRefresh(
    tokens: VkTokens,
    state: String,
    oauthRefresher: OAuthApiTokenRefresher,
): VkTokens {
    if (tokens.refreshToken != null && tokens.deviceId != null) {
        return oauthRefresher.refresh(tokens, state)
    }

    return coroutineScope {
        suspendCancellableCoroutine { continuation ->
            launch {
                VKID.instance.refreshToken(
                    callback = object : VKIDRefreshTokenCallback {
                        override fun onSuccess(token: AccessToken) {
                            if (continuation.isActive) {
                                continuation.resume(token.toVkTokens(tokens))
                            }
                        }

                        override fun onFail(fail: VKIDRefreshTokenFail) {
                            if (!continuation.isActive) return
                            continuation.resumeWithException(
                                TokenRefreshException(fail.toString()),
                            )
                        }
                    },
                )
            }
        }
    }
}

private fun AccessToken.toVkTokens(previous: VkTokens): VkTokens = VkTokens(
    accessToken = token,
    refreshToken = previous.refreshToken,
    userId = userID,
    expiresAtEpochSeconds = expireTime.takeIf { it > 0 },
    deviceId = previous.deviceId,
)
