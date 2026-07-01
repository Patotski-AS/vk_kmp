package com.vk.kmp.core.network

import io.ktor.client.plugins.logging.Logger

internal val SanitizedNetworkLogger = object : Logger {
    override fun log(message: String) {
        platformLogNetworkMessage(sanitizeNetworkLog(message))
    }
}

internal expect fun platformLogNetworkMessage(message: String)

internal fun sanitizeNetworkLog(message: String): String = message
    .replace(Regex("""(?i)(access_token=)[^&\s"]+"""), "$1***")
    .replace(Regex("""(?i)(refresh_token=)[^&\s"]+"""), "$1***")
    .replace(Regex("""(?i)(client_secret=)[^&\s"]+"""), "$1***")
    .replace(Regex("""(?i)("access_token"\s*:\s*")[^"]+(")"""), "$1***$2")
    .replace(Regex("""(?i)("refresh_token"\s*:\s*")[^"]+(")"""), "$1***$2")
