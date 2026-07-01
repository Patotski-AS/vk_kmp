package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.auth.api.AuthZoneController
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.auth.api.VkAppCredentials
import org.koin.dsl.module

val authModule = module {
    includes(platformAuthModule())

    single { TokenRefresher(get()) }
    single<VkAppCredentials> { platformVkAppCredentials() }
    single {
        SessionManager(
            tokenStorage = get(),
            tokenRefresher = get(),
        )
    }
    single<SessionRepository> { get<SessionManager>() }
    single<AuthResultHandler> { get<SessionManager>() }
    single<AuthZoneController> { get<SessionManager>() }
}
