package com.vk.kmp.core.network

internal actual fun isNetworkDebugLoggingEnabled(): Boolean =
    System.getProperty("vk.network.debug")?.toBoolean()
        ?: System.getenv("VK_NETWORK_DEBUG")?.toBoolean()
        ?: true

internal actual fun platformLogNetworkMessage(message: String) {
    println("[$NETWORK_LOG_TAG] $message")
}

private const val NETWORK_LOG_TAG = "VkHttp"
