package com.vk.kmp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun createHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun HttpClientConfig<*>.applyVkDefaults() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            },
        )
    }
    installDebugLoggingIfEnabled()
}

private fun HttpClientConfig<*>.installDebugLoggingIfEnabled() {
    if (!isNetworkDebugLoggingEnabled()) return

    install(Logging) {
        logger = SanitizedNetworkLogger
        level = LogLevel.ALL
    }
}
