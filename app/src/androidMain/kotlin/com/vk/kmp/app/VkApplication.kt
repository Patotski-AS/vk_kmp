package com.vk.kmp.app

import android.app.Application
import com.vk.kmp.data.storage.impl.initStorageContext
import com.vk.kmp.feature.login.impl.LoginPlatformInitializer

class VkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initStorageContext(this)
        LoginPlatformInitializer.onApplicationCreate(this)
    }
}
