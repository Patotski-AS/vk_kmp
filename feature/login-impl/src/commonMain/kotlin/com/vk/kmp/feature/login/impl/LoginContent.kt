package com.vk.kmp.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.vk.kmp.feature.login.api.LoginComponent
import com.vk.kmp.feature.login.api.LoginIntent

@Composable
fun LoginContent(component: LoginComponent) {
    val model by component.model.subscribeAsState()
    LoginScreen(
        model = model,
        onLoginClick = { component.onIntent(LoginIntent.OnLoginClick) },
    )
}
