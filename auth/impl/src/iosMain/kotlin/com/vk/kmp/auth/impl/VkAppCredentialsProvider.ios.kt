package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.VkAppCredentials

internal actual fun platformVkAppCredentials(): VkAppCredentials {
    val bundle = platform.Foundation.NSBundle.mainBundle
    val clientId = bundle.objectForInfoDictionaryKey("VKClientId") as? String ?: ""
    val clientSecret = bundle.objectForInfoDictionaryKey("VKClientSecret") as? String ?: ""
    return StaticVkAppCredentials(clientId, clientSecret)
}
