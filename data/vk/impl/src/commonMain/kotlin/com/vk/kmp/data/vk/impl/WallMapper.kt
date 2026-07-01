package com.vk.kmp.data.vk.impl

import com.vk.kmp.data.vk.api.WallPage
import com.vk.kmp.data.vk.api.WallPost
import com.vk.kmp.data.vk.api.dto.WallGetDto
import com.vk.kmp.data.vk.api.dto.WallItemDto

internal fun WallGetDto.toWallPage(offset: Int, requestedCount: Int): WallPage {
    val posts = items.map { it.toWallPost() }
    val nextOffset = if (items.size < requestedCount) null else offset + items.size
    return WallPage(posts = posts, nextOffset = nextOffset)
}

private fun WallItemDto.toWallPost(): WallPost = WallPost(
    id = id,
    ownerId = ownerId,
    text = text,
)
