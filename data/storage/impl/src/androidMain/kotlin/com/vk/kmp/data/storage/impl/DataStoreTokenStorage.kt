package com.vk.kmp.data.storage.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.data.storage.api.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "vk_tokens",
)

private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
private val KEY_USER_ID = longPreferencesKey("user_id")
private val KEY_EXPIRES_AT = longPreferencesKey("expires_at")
private val KEY_DEVICE_ID = stringPreferencesKey("device_id")

internal class DataStoreTokenStorage(
    private val context: Context,
) : TokenStorage {

    override fun getTokens(): VkTokens? = runBlocking(Dispatchers.IO) {
        val preferences = context.tokenDataStore.data.first()
        val accessToken = preferences[KEY_ACCESS_TOKEN] ?: return@runBlocking null
        VkTokens(
            accessToken = accessToken,
            refreshToken = preferences[KEY_REFRESH_TOKEN],
            userId = preferences[KEY_USER_ID] ?: return@runBlocking null,
            expiresAtEpochSeconds = preferences[KEY_EXPIRES_AT],
            deviceId = preferences[KEY_DEVICE_ID],
        )
    }

    override fun saveTokens(tokens: VkTokens) {
        val refreshToken = tokens.refreshToken
        val expiresAt = tokens.expiresAtEpochSeconds
        val deviceId = tokens.deviceId
        runBlocking(Dispatchers.IO) {
            context.tokenDataStore.edit { preferences ->
                preferences[KEY_ACCESS_TOKEN] = tokens.accessToken
                if (refreshToken != null) {
                    preferences[KEY_REFRESH_TOKEN] = refreshToken
                } else {
                    preferences.remove(KEY_REFRESH_TOKEN)
                }
                preferences[KEY_USER_ID] = tokens.userId
                if (expiresAt != null) {
                    preferences[KEY_EXPIRES_AT] = expiresAt
                } else {
                    preferences.remove(KEY_EXPIRES_AT)
                }
                if (deviceId != null) {
                    preferences[KEY_DEVICE_ID] = deviceId
                } else {
                    preferences.remove(KEY_DEVICE_ID)
                }
            }
        }
    }

    override fun clear() {
        runBlocking(Dispatchers.IO) {
            context.tokenDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }
}
