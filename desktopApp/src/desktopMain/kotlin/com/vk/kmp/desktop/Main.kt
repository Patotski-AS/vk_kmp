package com.vk.kmp.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vk.kmp.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VK KMP",
    ) {
        App()
    }
}
