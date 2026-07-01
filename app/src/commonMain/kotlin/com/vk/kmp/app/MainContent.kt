package com.vk.kmp.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vk.kmp.feature.feed.impl.FeedContent
import com.vk.kmp.feature.main.api.MainComponent

@Composable
fun MainContent(component: MainComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(
        stack = childStack,
        modifier = Modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            is MainComponent.Child.Feed -> FeedContent(component = instance.component)
        }
    }
}
