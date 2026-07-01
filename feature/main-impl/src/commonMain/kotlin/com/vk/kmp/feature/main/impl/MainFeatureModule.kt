package com.vk.kmp.feature.main.impl

import com.arkivanov.decompose.ComponentContext
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.main.api.MainComponent
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mainFeatureModule = module {
    factory<MainComponent> { (componentContext: ComponentContext) ->
        DefaultMainComponent(
            componentContext = componentContext,
            feedComponentFactory = { childContext ->
                get<FeedComponent> { parametersOf(childContext) }
            },
        )
    }
}
