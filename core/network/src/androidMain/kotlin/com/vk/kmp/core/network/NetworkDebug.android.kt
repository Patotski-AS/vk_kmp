package com.vk.kmp.core.network

import android.util.Log

internal actual fun isNetworkDebugLoggingEnabled(): Boolean = BuildConfig.DEBUG

internal actual fun platformLogNetworkMessage(message: String) {
    Log.d(NETWORK_LOG_TAG, message)
}

private const val NETWORK_LOG_TAG = "VkHttp"
