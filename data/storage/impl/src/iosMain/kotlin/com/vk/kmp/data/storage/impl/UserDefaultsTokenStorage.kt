package com.vk.kmp.data.storage.impl

import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.data.storage.api.TokenStorage
import platform.Foundation.NSUserDefaults

private const val PREFS_NAME = "vk_kmp_tokens"
private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_USER_ID = "user_id"
private const val KEY_EXPIRES_AT = "expires_at"
private const val KEY_DEVICE_ID = "device_id"

internal class UserDefaultsTokenStorage : TokenStorage {
    private val defaults = NSUserDefaults(suiteName = PREFS_NAME) ?: NSUserDefaults.standardUserDefaults

    override fun getTokens(): VkTokens? {
        val accessToken = defaults.stringForKey(KEY_ACCESS_TOKEN) ?: return null
        val userId = defaults.objectForKey(KEY_USER_ID) as? Long ?: return null
        val expiresAt = defaults.objectForKey(KEY_EXPIRES_AT) as? Long
        return VkTokens(
            accessToken = accessToken,
            refreshToken = defaults.stringForKey(KEY_REFRESH_TOKEN),
            userId = userId,
            expiresAtEpochSeconds = expiresAt,
            deviceId = defaults.stringForKey(KEY_DEVICE_ID),
        )
    }

    override fun saveTokens(tokens: VkTokens) {
        val refreshToken = tokens.refreshToken
        val expiresAt = tokens.expiresAtEpochSeconds
        val deviceId = tokens.deviceId
        defaults.setObject(tokens.accessToken, KEY_ACCESS_TOKEN)
        if (refreshToken != null) {
            defaults.setObject(refreshToken, KEY_REFRESH_TOKEN)
        } else {
            defaults.removeObjectForKey(KEY_REFRESH_TOKEN)
        }
        defaults.setObject(tokens.userId, KEY_USER_ID)
        if (expiresAt != null) {
            defaults.setObject(expiresAt, KEY_EXPIRES_AT)
        } else {
            defaults.removeObjectForKey(KEY_EXPIRES_AT)
        }
        if (deviceId != null) {
            defaults.setObject(deviceId, KEY_DEVICE_ID)
        } else {
            defaults.removeObjectForKey(KEY_DEVICE_ID)
        }
        defaults.synchronize()
    }

    override fun clear() {
        listOf(
            KEY_ACCESS_TOKEN,
            KEY_REFRESH_TOKEN,
            KEY_USER_ID,
            KEY_EXPIRES_AT,
            KEY_DEVICE_ID,
        ).forEach(defaults::removeObjectForKey)
        defaults.synchronize()
    }
}
