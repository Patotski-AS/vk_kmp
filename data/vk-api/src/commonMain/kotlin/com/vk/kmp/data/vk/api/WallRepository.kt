package com.vk.kmp.data.vk.api

data class WallPost(
    val id: Long,
    val ownerId: Long,
    val text: String,
)

data class WallPage(
    val posts: List<WallPost>,
    val nextOffset: Int?,
)

interface WallRepository {
    suspend fun getWall(ownerId: Long, offset: Int, count: Int): WallPage
}
