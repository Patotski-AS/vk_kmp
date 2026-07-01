package com.vk.kmp.feature.feed.impl

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.auth.api.SessionState
import com.vk.kmp.data.vk.api.WallPage
import com.vk.kmp.data.vk.api.WallRepository
import com.vk.kmp.feature.feed.api.FeedIntent
import com.vk.kmp.feature.feed.api.FeedModel
import com.vk.kmp.feature.feed.api.FeedPostItem
import kotlinx.coroutines.launch

internal interface FeedStore : Store<FeedIntent, FeedModel, Nothing>

class FeedStoreFactory(
    private val storeFactory: StoreFactory,
    private val wallRepository: WallRepository,
    private val sessionRepository: SessionRepository,
) {
    fun create(): Store<FeedIntent, FeedModel, Nothing> = object : FeedStore,
        Store<FeedIntent, FeedModel, Nothing> by storeFactory.create(
            name = "FeedStore",
            initialState = FeedModel(isLoading = true),
            bootstrapper = SimpleBootstrapper(FeedIntent.Load),
            executorFactory = {
                FeedExecutor(
                    wallRepository = wallRepository,
                    sessionRepository = sessionRepository,
                )
            },
            reducer = FeedReducer,
        ) {}
}

private class FeedExecutor(
    private val wallRepository: WallRepository,
    private val sessionRepository: SessionRepository,
) : CoroutineExecutor<FeedIntent, FeedIntent, FeedModel, Message, Nothing>() {

    override fun executeAction(action: FeedIntent) {
        executeIntent(action)
    }

    override fun executeIntent(intent: FeedIntent) {
        when (intent) {
            FeedIntent.Load -> load(refreshing = false, append = false)
            FeedIntent.Refresh -> load(refreshing = true, append = false)
            FeedIntent.LoadMore -> load(refreshing = false, append = true)
        }
    }

    private fun load(refreshing: Boolean, append: Boolean) {
        val currentState = state()

        if (append) {
            if (currentState.isLoadingMore || currentState.endReached || currentState.isLoading) return
            dispatch(Message.LoadingMore)
        } else {
            dispatch(Message.LoadStarted(refreshing))
        }

        scope.launch {
            runCatching {
                val userId = when (val session = sessionRepository.sessionState.value) {
                    is SessionState.Authenticated -> session.userId
                    else -> error("Feed requires authenticated session")
                }
                val offset = if (append) currentState.nextOffset else 0
                wallRepository.getWall(ownerId = userId, offset = offset, count = PAGE_SIZE)
            }.onSuccess { page ->
                dispatch(Message.PageLoaded(page, append))
            }.onFailure { error ->
                dispatch(Message.Error(error.message ?: "Unknown error", append))
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

private sealed interface Message {
    data class LoadStarted(val refreshing: Boolean) : Message
    data object LoadingMore : Message
    data class PageLoaded(val page: WallPage, val append: Boolean) : Message
    data class Error(val message: String, val append: Boolean) : Message
}

private object FeedReducer : Reducer<FeedModel, Message> {
    override fun FeedModel.reduce(msg: Message): FeedModel = when (msg) {
        is Message.LoadStarted -> copy(
            isLoading = !msg.refreshing && posts.isEmpty(),
            isRefreshing = msg.refreshing,
            isLoadingMore = false,
            error = if (msg.refreshing) error else null,
        )

        is Message.LoadingMore -> copy(
            isLoadingMore = true,
            error = null,
        )

        is Message.PageLoaded -> {
            val mappedPosts = msg.page.posts.map { post ->
                FeedPostItem(id = post.id, text = post.text)
            }
            copy(
                posts = if (msg.append) posts + mappedPosts else mappedPosts,
                isLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                error = null,
                nextOffset = msg.page.nextOffset ?: if (msg.append) nextOffset else 0,
                endReached = msg.page.nextOffset == null,
            )
        }

        is Message.Error -> if (msg.append) {
            copy(
                isLoadingMore = false,
                error = msg.message,
            )
        } else {
            copy(
                isLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                error = msg.message,
            )
        }
    }
}
