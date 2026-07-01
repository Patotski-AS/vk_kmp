package com.vk.kmp.data.vk.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VkApiResponse<T>(
    val response: T? = null,
    val error: VkApiError? = null,
)

@Serializable
data class VkApiError(
    @SerialName("error_code") val errorCode: Int,
    @SerialName("error_msg") val errorMsg: String,
)

@Serializable
data class WallGetDto(
    val count: Int,
    val items: List<WallItemDto> = emptyList(),
)

@Serializable
data class WallItemDto(
    val id: Long,
    @SerialName("owner_id") val ownerId: Long,
    val text: String = "",
)
