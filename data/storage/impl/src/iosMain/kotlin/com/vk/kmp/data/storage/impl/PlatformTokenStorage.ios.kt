package com.vk.kmp.data.storage.impl

import com.vk.kmp.data.storage.api.TokenStorage

internal actual fun createTokenStorage(): TokenStorage = UserDefaultsTokenStorage()
