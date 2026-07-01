package com.vk.kmp.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.vk.kmp.auth.api.AuthZoneController
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.auth.impl.authModule
import com.vk.kmp.core.navigation.DefaultRootComponent
import com.vk.kmp.core.navigation.RootComponent
import com.vk.kmp.core.navigation.RootIntent
import com.vk.kmp.core.ui.VkTheme
import com.vk.kmp.data.storage.impl.storageModule
import com.vk.kmp.data.vk.impl.vkDataModule
import com.vk.kmp.feature.feed.impl.feedFeatureModule
import com.vk.kmp.feature.login.api.LoginComponent
import com.vk.kmp.feature.login.impl.LoginContent
import com.vk.kmp.feature.login.impl.loginFeatureModule
import com.vk.kmp.feature.main.api.MainComponent
import com.vk.kmp.feature.main.impl.mainFeatureModule
import org.koin.compose.KoinApplication
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun App() {
    KoinApplication(application = {
        modules(
            storageModule,
            authModule,
            vkDataModule,
            loginFeatureModule,
            mainFeatureModule,
            feedFeatureModule,
            appModule,
        )
    }) {
        VkTheme {
            val root = createRootComponent()
            RootContent(root)
        }
    }
}

@Composable
private fun RootContent(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(
        stack = childStack,
        modifier = Modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Login -> LoginContent(component = instance.component)
            is RootComponent.Child.Main -> MainContent(
                component = instance.component,
                onLogout = { component.onIntent(RootIntent.Logout) },
            )
        }
    }
}

@Composable
private fun createRootComponent(): RootComponent {
    val koin = getKoin()
    val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(lifecycle = lifecycle)

    return DefaultRootComponent(
        componentContext = componentContext,
        authZoneController = koin.get<AuthZoneController>(),
        sessionRepository = koin.get<SessionRepository>(),
        loginComponentFactory = { childContext ->
            koin.get<LoginComponent> { parametersOf(childContext) }
        },
        mainComponentFactory = { childContext ->
            koin.get<MainComponent> { parametersOf(childContext) }
        },
    )
}

private val appModule = module {
    // Reserved for RootComponent factory wiring if moved from composable.
}
