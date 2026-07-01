package com.vk.kmp.feature.feed.api

import com.arkivanov.decompose.value.Value

interface FeedComponent {
    val model: Value<FeedModel>
    fun onIntent(intent: FeedIntent)
}

data class FeedModel(
    val posts: List<FeedPostItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
)

data class FeedPostItem(
    val id: Long,
    val text: String,
)

sealed interface FeedIntent {
    data object Load : FeedIntent
    data object Refresh : FeedIntent
    data object LoadMore : FeedIntent
}
