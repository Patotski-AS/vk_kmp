package com.vk.kmp.feature.login.impl

import com.arkivanov.decompose.ComponentContext
import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.feature.login.api.LoginComponent
import org.koin.dsl.module

class StubAuthLauncher : AuthLauncher {
    override suspend fun launch(): AuthLaunchResult =
        AuthLaunchResult.Success(
            VkTokens(
                accessToken = "stub-token",
                refreshToken = null,
                userId = 1L,
                expiresAtEpochSeconds = null,
            ),
        )
}

val loginFeatureModule = module {
    factory<AuthLauncher> { StubAuthLauncher() }
    factory<LoginComponent> { (componentContext: ComponentContext) ->
        DefaultLoginComponent(
            componentContext = componentContext,
            authLauncher = get(),
            authResultHandler = get(),
        )
    }
}
