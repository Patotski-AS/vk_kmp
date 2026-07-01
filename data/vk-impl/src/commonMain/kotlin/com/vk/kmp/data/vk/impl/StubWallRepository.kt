package com.vk.kmp.data.vk.impl

import com.vk.kmp.data.vk.api.WallPage
import com.vk.kmp.data.vk.api.WallRepository

class StubWallRepository : WallRepository {
    override suspend fun getWall(ownerId: Long, offset: Int, count: Int): WallPage =
        WallPage(posts = emptyList(), nextOffset = null)
}
