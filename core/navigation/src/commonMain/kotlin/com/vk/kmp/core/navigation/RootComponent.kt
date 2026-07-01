package com.vk.kmp.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.vk.kmp.feature.login.api.LoginComponent
import com.vk.kmp.feature.main.api.MainComponent

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun onIntent(intent: RootIntent)

    sealed interface Child {
        data class Login(val component: LoginComponent) : Child
        data class Main(val component: MainComponent) : Child
    }
}

sealed interface RootIntent {
    data object Logout : RootIntent
}

fun interface RootComponentFactory {
    fun create(componentContext: ComponentContext): RootComponent
}
