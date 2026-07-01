package com.vk.kmp.feature.feed.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.vk.kmp.feature.feed.api.FeedComponent
import com.vk.kmp.feature.feed.api.FeedIntent
import com.vk.kmp.feature.feed.api.FeedModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultFeedComponent(
    componentContext: ComponentContext,
    feedStoreFactory: FeedStoreFactory,
) : FeedComponent, ComponentContext by componentContext {

    private val store = feedStoreFactory.create()

    private val _model = MutableValue(store.state)
    override val model: Value<FeedModel> = _model

    init {
        store.stateFlow(lifecycle)
            .onEach { _model.value = it }
            .launchIn(coroutineScope())

        lifecycle.doOnDestroy(store::dispose)
    }

    override fun onIntent(intent: FeedIntent) {
        store.accept(intent)
    }
}
