package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow
import org.koin.dsl.module

internal actual fun platformAuthModule() = module {
    single<VkOAuthFlow> { UnsupportedVkOAuthFlow() }
}
