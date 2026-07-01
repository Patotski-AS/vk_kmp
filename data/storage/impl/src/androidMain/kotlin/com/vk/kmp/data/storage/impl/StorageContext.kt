package com.vk.kmp.data.storage.impl

import android.content.Context

private var applicationContext: Context? = null

fun initStorageContext(context: Context) {
    applicationContext = context.applicationContext
}

internal fun requireStorageContext(): Context =
    checkNotNull(applicationContext) {
        "Storage context is not initialized. Call initStorageContext() from Application.onCreate()."
    }
