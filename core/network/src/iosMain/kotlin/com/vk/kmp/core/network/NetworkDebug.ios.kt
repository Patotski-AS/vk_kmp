package com.vk.kmp.core.network

import kotlin.native.Platform

internal actual fun isNetworkDebugLoggingEnabled(): Boolean = Platform.isDebugBinary

internal actual fun platformLogNetworkMessage(message: String) {
    println("[$NETWORK_LOG_TAG] $message")
}

private const val NETWORK_LOG_TAG = "VkHttp"
