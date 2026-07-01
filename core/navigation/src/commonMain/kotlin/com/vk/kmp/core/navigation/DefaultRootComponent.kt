package com.vk.kmp.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.vk.kmp.auth.api.AuthZone
import com.vk.kmp.auth.api.AuthZoneController
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.feature.login.api.LoginComponent
import com.vk.kmp.feature.main.api.MainComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val authZoneController: AuthZoneController,
    private val sessionRepository: SessionRepository,
    private val loginComponentFactory: (ComponentContext) -> LoginComponent,
    private val mainComponentFactory: (ComponentContext) -> MainComponent,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = initialConfig(),
            handleBackButton = false,
            childFactory = ::child,
        )

    init {
        authZoneController.zone
            .onEach { zone ->
                when (zone) {
                    AuthZone.Unauthorized -> navigation.replaceAll(Config.Login)
                    AuthZone.Authorized -> navigation.replaceAll(Config.Main)
                }
            }
            .launchIn(coroutineScope())
    }

    override fun onIntent(intent: RootIntent) {
        when (intent) {
            RootIntent.Logout -> coroutineScope().launch {
                sessionRepository.logout()
            }
        }
    }

    private fun initialConfig(): Config =
        when (authZoneController.zone.value) {
            AuthZone.Unauthorized -> Config.Login
            AuthZone.Authorized -> Config.Main
        }

    private fun child(config: Config, childContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Login -> RootComponent.Child.Login(loginComponentFactory(childContext))
            Config.Main -> RootComponent.Child.Main(mainComponentFactory(childContext))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Login : Config

        @Serializable
        data object Main : Config
    }
}
