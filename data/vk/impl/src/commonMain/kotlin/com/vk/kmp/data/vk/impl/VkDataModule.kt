package com.vk.kmp.data.vk.impl

import com.vk.kmp.data.vk.api.WallRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val vkDataModule = module {
    single { VkHttpClientFactory(get()) }
    single { get<VkHttpClientFactory>().create() }
    single { VkApi(get()) }
    single<WallRepository> { WallRepositoryImpl(get()) }
}
