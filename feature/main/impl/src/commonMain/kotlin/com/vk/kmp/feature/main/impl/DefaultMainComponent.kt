package com.vk.kmp.feature.main.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.main.api.MainComponent
import kotlinx.serialization.Serializable

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val feedComponentFactory: (ComponentContext) -> FeedComponent,
) : MainComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, MainComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Feed,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, childContext: ComponentContext): MainComponent.Child =
        when (config) {
            Config.Feed -> MainComponent.Child.Feed(feedComponentFactory(childContext))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Feed : Config
    }
}
