package com.vk.kmp.feature.feed.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vk.kmp.core.ui.PlaceholderScreen
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.feed.api.FeedIntent
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(component: FeedComponent) {
    val model by component.model.subscribeAsState()

    when {
        model.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        model.error != null && model.posts.isEmpty() -> {
            PlaceholderScreen(
                title = model.error ?: "Ошибка",
                modifier = Modifier.fillMaxSize(),
                action = {
                    Button(onClick = { component.onIntent(FeedIntent.Load) }) {
                        Text("Повторить")
                    }
                },
            )
        }

        model.posts.isEmpty() -> {
            PullToRefreshBox(
                isRefreshing = model.isRefreshing,
                onRefresh = { component.onIntent(FeedIntent.Refresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                PlaceholderScreen(
                    title = "Лента пуста",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        else -> {
            FeedList(
                component = component,
                model = model,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedList(
    component: FeedComponent,
    model: com.vk.kmp.feature.feed.api.FeedModel,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, model.posts.size, model.endReached, model.isLoadingMore, model.isLoading) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= totalItems - LOAD_MORE_THRESHOLD
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd && !model.endReached && !model.isLoadingMore && !model.isLoading) {
                    component.onIntent(FeedIntent.LoadMore)
                }
            }
    }

    PullToRefreshBox(
        isRefreshing = model.isRefreshing,
        onRefresh = { component.onIntent(FeedIntent.Refresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(model.posts, key = { it.id }) { post ->
                Text(
                    text = post.text.ifBlank { "(без текста)" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }

            if (model.isLoadingMore) {
                item(key = "loading_more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

private const val LOAD_MORE_THRESHOLD = 3
