package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.auth.api.AuthZoneController
import com.vk.kmp.auth.api.SessionRepository
import org.koin.dsl.module

val authModule = module {
    single {
        SessionManager(get())
    }
    single<SessionRepository> { get<SessionManager>() }
    single<AuthResultHandler> { get<SessionManager>() }
    single<AuthZoneController> { get<SessionManager>() }
}
