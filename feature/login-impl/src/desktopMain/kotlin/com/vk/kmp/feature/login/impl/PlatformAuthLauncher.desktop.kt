package com.vk.kmp.feature.login.impl

import com.vk.kmp.auth.api.AuthLauncher

internal actual class PlatformAuthLauncher actual constructor() : AuthLauncher {
    private val stub = StubAuthLauncher()

    override suspend fun launch() = stub.launch()
}
