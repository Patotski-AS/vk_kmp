package com.vk.kmp.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vk.kmp.feature.feed.impl.FeedContent
import com.vk.kmp.feature.main.api.MainComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    component: MainComponent,
    onLogout: () -> Unit,
) {
    val childStack by component.childStack.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Лента") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Выйти")
                    }
                },
            )
        },
    ) { padding ->
        Children(
            stack = childStack,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { child ->
            when (val instance = child.instance) {
                is MainComponent.Child.Feed -> FeedContent(component = instance.component)
            }
        }
    }
}
