package com.vk.kmp.feature.login.impl

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.vk.kmp.auth.api.AuthLauncher
import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.feature.login.api.LoginComponent
import com.vk.kmp.feature.login.api.LoginIntent
import com.vk.kmp.feature.login.api.LoginModel
import kotlinx.coroutines.launch

class DefaultLoginComponent(
    componentContext: ComponentContext,
    private val authLauncher: AuthLauncher,
    private val authResultHandler: AuthResultHandler,
) : LoginComponent, ComponentContext by componentContext {

    private val _model = MutableValue(LoginModel())
    override val model: Value<LoginModel> = _model

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.OnLoginClick -> launchAuth()
        }
    }

    private fun launchAuth() {
        _model.update { it.copy(isLoading = true, error = null) }
        coroutineScope().launch {
            when (val result = authLauncher.launch()) {
                is AuthLaunchResult.Success -> authResultHandler.onLoginSuccess(result.tokens)
                AuthLaunchResult.Cancelled -> _model.update { it.copy(isLoading = false) }
                is AuthLaunchResult.Error -> _model.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
