package com.vk.kmp.feature.login.impl

import com.arkivanov.decompose.ComponentContext
import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.feature.login.api.LoginComponent
import org.koin.dsl.module

val loginFeatureModule = module {
    factory<AuthLauncher> { PlatformAuthLauncher() }
    factory<LoginComponent> { (componentContext: ComponentContext) ->
        DefaultLoginComponent(
            componentContext = componentContext,
            authLauncher = get(),
            authResultHandler = get(),
        )
    }
}
