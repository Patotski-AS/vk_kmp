package com.vk.kmp.data.vk.impl

import com.vk.kmp.data.vk.api.WallPage
import com.vk.kmp.data.vk.api.WallRepository

internal class WallRepositoryImpl(
    private val vkApi: VkApi,
) : WallRepository {
    override suspend fun getWall(ownerId: Long, offset: Int, count: Int): WallPage {
        val response = vkApi.wallGet(ownerId = ownerId, offset = offset, count = count)
        return response.toWallPage(offset = offset, requestedCount = count)
    }
}
