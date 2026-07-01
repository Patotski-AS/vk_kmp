package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.AuthLaunchResult
import com.vk.kmp.auth.api.VkAppCredentials
import com.vk.kmp.auth.api.VkOAuthFlow
import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.core.network.applyVkDefaults
import com.vk.kmp.core.network.createHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import com.sun.net.httpserver.HttpServer

internal object PkceGenerator {
    private val secureRandom = SecureRandom()

    fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.toByteArray(StandardCharsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    fun generateState(): String {
        val bytes = ByteArray(24)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

internal object BrowserLauncher {
    fun openUrl(url: String) {
        val desktop = java.awt.Desktop.getDesktop()
        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            error("Desktop browse is not supported")
        }
        desktop.browse(URI(url))
    }
}

internal data class OAuthCallback(
    val code: String?,
    val deviceId: String?,
    val state: String?,
    val error: String?,
    val errorDescription: String?,
)

internal class LocalCallbackServer {
    private val server: HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    private val port = server.address.port
    val redirectUri: String = "http://127.0.0.1:$port${OAuthConstants.CALLBACK_PATH}"

    private var result: Result<OAuthCallback>? = null
    private val lock = Object()

    init {
        server.createContext(OAuthConstants.CALLBACK_PATH) { exchange ->
            val query = exchange.requestURI.rawQuery.orEmpty()
            val params = query.split("&")
                .mapNotNull { part ->
                    val chunks = part.split("=", limit = 2)
                    if (chunks.size == 2) chunks[0] to chunks[1] else null
                }
                .toMap()

            val callback = OAuthCallback(
                code = params["code"],
                deviceId = params["device_id"],
                state = params["state"],
                error = params["error"],
                errorDescription = params["error_description"],
            )

            synchronized(lock) {
                result = Result.success(callback)
                lock.notifyAll()
            }

            val response = "Authorization complete. You can close this tab."
            val bytes = response.toByteArray(StandardCharsets.UTF_8)
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
            exchange.close()
        }
        server.executor = null
        server.start()
    }

    suspend fun awaitCallback(): OAuthCallback = withContext(Dispatchers.IO) {
        withTimeout(5 * 60 * 1000L) {
            synchronized(lock) {
                while (result == null) {
                    lock.wait(1_000)
                }
                result!!.getOrThrow()
            }
        }
    }

    fun close() {
        server.stop(0)
    }
}

internal class OAuthClient(
    private val credentials: VkAppCredentials,
) : VkOAuthFlow {

    private val httpClient = createHttpClient {
        applyVkDefaults()
    }

    override suspend fun authorize(): AuthLaunchResult {
        if (!credentials.isConfigured) {
            return AuthLaunchResult.Error("VK credentials are not configured")
        }

        val codeVerifier = PkceGenerator.generateCodeVerifier()
        val codeChallenge = PkceGenerator.generateCodeChallenge(codeVerifier)
        val state = PkceGenerator.generateState()
        val callbackServer = LocalCallbackServer()

        return try {
            val authorizeUrl = buildAuthorizeUrl(
                redirectUri = callbackServer.redirectUri,
                state = state,
                codeChallenge = codeChallenge,
            )
            BrowserLauncher.openUrl(authorizeUrl)

            val callback = callbackServer.awaitCallback()
            if (callback.error != null) {
                return AuthLaunchResult.Error(callback.errorDescription ?: callback.error)
            }

            val code = callback.code
                ?: return AuthLaunchResult.Error("Authorization code is missing")
            val deviceId = callback.deviceId
                ?: return AuthLaunchResult.Error("Device id is missing")
            if (callback.state != state) {
                return AuthLaunchResult.Error("OAuth state mismatch")
            }

            val tokens = exchangeCode(
                code = code,
                codeVerifier = codeVerifier,
                redirectUri = callbackServer.redirectUri,
                deviceId = deviceId,
                state = state,
            )
            AuthLaunchResult.Success(tokens)
        } catch (_: TimeoutCancellationException) {
            AuthLaunchResult.Cancelled
        } catch (throwable: Throwable) {
            AuthLaunchResult.Error(throwable.message ?: "OAuth failed")
        } finally {
            callbackServer.close()
        }
    }

    private fun buildAuthorizeUrl(
        redirectUri: String,
        state: String,
        codeChallenge: String,
    ): String = buildString {
        append(OAuthConstants.AUTHORIZE_URL)
        append("?response_type=code")
        append("&client_id=").append(credentials.clientId.encodeUrl())
        append("&redirect_uri=").append(redirectUri.encodeUrl())
        append("&state=").append(state.encodeUrl())
        append("&code_challenge=").append(codeChallenge.encodeUrl())
        append("&code_challenge_method=S256")
        append("&scope=").append("vkid.personal_info".encodeUrl())
    }

    private suspend fun exchangeCode(
        code: String,
        codeVerifier: String,
        redirectUri: String,
        deviceId: String,
        state: String,
    ): VkTokens {
        val parameters = Parameters.build {
            append("grant_type", "authorization_code")
            append("code_verifier", codeVerifier)
            append("redirect_uri", redirectUri)
            append("code", code)
            append("client_id", credentials.clientId)
            append("device_id", deviceId)
            append("state", state)
            append("client_secret", credentials.clientSecret)
        }

        val response = httpClient.post(OAuthConstants.TOKEN_URL) {
            setBody(FormDataContent(parameters))
        }.body<VkTokenResponse>()

        if (response.state != null && response.state != state) {
            throw IllegalStateException("OAuth state mismatch")
        }

        val expiresAt = response.expiresIn?.let { expiresIn ->
            Clock.System.now().epochSeconds + expiresIn
        }

        return VkTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            userId = response.userId,
            expiresAtEpochSeconds = expiresAt,
            deviceId = deviceId,
        )
    }

    private fun String.encodeUrl(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)
}
