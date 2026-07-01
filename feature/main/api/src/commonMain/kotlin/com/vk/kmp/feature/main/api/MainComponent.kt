package com.vk.kmp.feature.main.api

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.vk.kmp.feature.feed.api.FeedComponent

interface MainComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Feed(val component: FeedComponent) : Child
    }
}
