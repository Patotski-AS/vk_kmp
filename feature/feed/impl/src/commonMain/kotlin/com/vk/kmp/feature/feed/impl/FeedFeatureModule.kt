package com.vk.kmp.feature.feed.impl

import com.arkivanov.decompose.ComponentContext
import com.vk.kmp.feature.feed.api.FeedComponent
import org.koin.dsl.module

val feedFeatureModule = module {
    factory<FeedComponent> { (componentContext: ComponentContext) ->
        DefaultFeedComponent(
            componentContext = componentContext,
            wallRepository = get(),
            sessionRepository = get(),
        )
    }
}
