package com.vk.kmp.feature.feed.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.vk.kmp.feature.feed.api.FeedComponent
import org.koin.dsl.module

val feedFeatureModule = module {
    single<StoreFactory> { DefaultStoreFactory() }
    factory { FeedStoreFactory(storeFactory = get(), wallRepository = get(), sessionRepository = get()) }
    factory<FeedComponent> { (componentContext: ComponentContext) ->
        DefaultFeedComponent(
            componentContext = componentContext,
            feedStoreFactory = get(),
        )
    }
}
