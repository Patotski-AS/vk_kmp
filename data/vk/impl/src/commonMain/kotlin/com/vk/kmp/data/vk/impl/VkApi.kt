package com.vk.kmp.data.vk.impl

import com.vk.kmp.data.vk.api.dto.VkApiResponse
import com.vk.kmp.data.vk.api.dto.WallGetDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class VkApi(
    private val httpClient: HttpClient,
) {
    suspend fun wallGet(ownerId: Long, offset: Int, count: Int): WallGetDto {
        val envelope = httpClient.get("${VK_API_BASE_URL}wall.get") {
            parameter("owner_id", ownerId)
            parameter("offset", offset)
            parameter("count", count)
            parameter("v", VK_API_VERSION)
        }.body<VkApiResponse<WallGetDto>>()

        envelope.error?.let { error ->
            throw VkApiException("${error.errorMsg} (${error.errorCode})")
        }

        return envelope.response ?: throw VkApiException("Empty VK API response")
    }
}
