package com.vk.kmp.feature.feed.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vk.kmp.core.ui.PlaceholderScreen
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.feed.api.FeedIntent

@Composable
fun FeedContent(component: FeedComponent) {
    val model by component.model.subscribeAsState()

    when {
        model.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        model.error != null -> {
            PlaceholderScreen(title = model.error ?: "Error")
        }

        model.posts.isEmpty() -> {
            PlaceholderScreen(title = "Лента пуста")
        }

        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(model.posts, key = { it.id }) { post ->
                    Text(
                        text = post.text.ifBlank { "(без текста)" },
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }

    if (model.isRefreshing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}
