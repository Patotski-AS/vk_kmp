package com.vk.kmp.feature.login.api

import com.arkivanov.decompose.value.Value

interface LoginComponent {
    val model: Value<LoginModel>
    fun onIntent(intent: LoginIntent)
}

data class LoginModel(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface LoginIntent {
    data object OnLoginClick : LoginIntent
}
