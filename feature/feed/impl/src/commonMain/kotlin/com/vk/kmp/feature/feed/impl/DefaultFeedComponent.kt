package com.vk.kmp.feature.feed.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.data.vk.api.WallRepository
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.feed.api.FeedIntent
import com.vk.kmp.feature.feed.api.FeedModel
import com.vk.kmp.feature.feed.api.FeedPostItem
import kotlinx.coroutines.launch

class DefaultFeedComponent(
    componentContext: ComponentContext,
    private val wallRepository: WallRepository,
    private val sessionRepository: SessionRepository,
) : FeedComponent, ComponentContext by componentContext {

    private val _model = MutableValue(FeedModel(isLoading = true))
    override val model: Value<FeedModel> = _model

    init {
        onIntent(FeedIntent.Load)
    }

    override fun onIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Load -> load(refreshing = false)
            FeedIntent.Refresh -> load(refreshing = true)
            FeedIntent.LoadMore -> Unit
        }
    }

    private fun load(refreshing: Boolean) {
        _model.update {
            it.copy(
                isLoading = !refreshing,
                isRefreshing = refreshing,
                error = null,
            )
        }

        coroutineScope().launch {
            runCatching {
                val userId = when (val state = sessionRepository.sessionState.value) {
                    is com.vk.kmp.auth.api.SessionState.Authenticated -> state.userId
                    else -> error("Feed requires authenticated session")
                }
                wallRepository.getWall(ownerId = userId, offset = 0, count = 20)
            }.onSuccess { page ->
                _model.update {
                    it.copy(
                        posts = page.posts.map { post ->
                            FeedPostItem(id = post.id, text = post.text)
                        },
                        isLoading = false,
                        isRefreshing = false,
                        endReached = page.nextOffset == null,
                    )
                }
            }.onFailure { error ->
                _model.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = error.message ?: "Unknown error",
                    )
                }
            }
        }
    }
}
