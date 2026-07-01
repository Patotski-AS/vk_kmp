package com.vk.kmp.data.storage.impl

import com.vk.kmp.data.storage.api.TokenStorage
import org.koin.dsl.bind
import org.koin.dsl.module

val storageModule = module {
    single<TokenStorage> { createTokenStorage() }
}
