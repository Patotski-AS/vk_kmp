package com.vk.kmp.feature.login.impl

import android.app.Application
import com.vk.id.VKID

object LoginPlatformInitializer {
    fun onApplicationCreate(application: Application) {
        if (VkIdCredentials.isConfigured) {
            VKID.init(application)
        }
    }
}
