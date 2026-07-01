package com.vk.kmp.data.vk.impl

import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.core.network.applyVkDefaults
import com.vk.kmp.core.network.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class VkHttpClientFactory(
    private val sessionRepository: SessionRepository,
) {
    fun create(): HttpClient = createHttpClient {
        applyVkDefaults()
        install(createVkAuthPlugin(sessionRepository))
    }
}

private fun createVkAuthPlugin(sessionRepository: SessionRepository): ClientPlugin<Unit> =
    createClientPlugin("VkAuth") {
        on(Send) { request ->
            val token = sessionRepository.getValidAccessToken()
            appendAccessToken(request, token)

            val firstCall = proceed(request)
            if (!firstCall.response.isAuthFailure()) {
                return@on firstCall
            }

            val refreshedToken = runCatching { sessionRepository.refreshAccessToken() }.getOrElse {
                sessionRepository.logout()
                return@on firstCall
            }

            appendAccessToken(request, refreshedToken)
            val retryCall = proceed(request)
            if (retryCall.response.isAuthFailure()) {
                sessionRepository.logout()
            }
            retryCall
        }
    }

private fun appendAccessToken(request: HttpRequestBuilder, token: String) {
    val currentUrl = request.url.build()
    if (currentUrl.parameters.contains("access_token")) return

    request.url {
        takeFrom(currentUrl)
        parameters.append("access_token", token)
    }
}

private suspend fun HttpResponse.isAuthFailure(): Boolean {
    if (status == HttpStatusCode.Unauthorized) return true
    val body = runCatching { bodyAsText() }.getOrDefault("")
    return runCatching {
        Json.parseToJsonElement(body)
            .jsonObject["error"]
            ?.jsonObject
            ?.get("error_code")
            ?.jsonPrimitive
            ?.intOrNull == VK_AUTH_ERROR_CODE
    }.getOrDefault(false)
}

private const val VK_AUTH_ERROR_CODE = 5
