package com.vk.kmp.auth.impl

import com.vk.kmp.auth.api.AuthResultHandler
import com.vk.kmp.auth.api.AuthZone
import com.vk.kmp.auth.api.AuthZoneController
import com.vk.kmp.auth.api.SessionRepository
import com.vk.kmp.auth.api.SessionState
import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.data.storage.api.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class SessionManager internal constructor(
    private val tokenStorage: TokenStorage,
    private val tokenRefresher: TokenRefresher,
) : SessionRepository, AuthResultHandler, AuthZoneController {

    private val mutex = Mutex()

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val _zone = MutableStateFlow(AuthZone.Unauthorized)
    override val zone: StateFlow<AuthZone> = _zone.asStateFlow()

    init {
        restoreSession()
    }

    override suspend fun onLoginSuccess(tokens: VkTokens) {
        mutex.withLock {
            tokenStorage.saveTokens(tokens)
            updateAuthenticated(tokens.userId, tokens.expiresAtEpochSeconds)
        }
    }

    override suspend fun getValidAccessToken(): String = mutex.withLock {
        val tokens = tokenStorage.getTokens()
            ?: throw IllegalStateException("No active session")

        if (tokens.isValid()) {
            return tokens.accessToken
        }

        return refreshTokens(tokens)
    }

    override suspend fun logout() {
        mutex.withLock {
            tokenStorage.clear()
            _sessionState.value = SessionState.Unauthenticated
            _zone.value = AuthZone.Unauthorized
        }
    }

    private fun restoreSession() {
        val tokens = tokenStorage.getTokens() ?: return
        updateAuthenticated(tokens.userId, tokens.expiresAtEpochSeconds)
    }

    private suspend fun refreshTokens(tokens: VkTokens): String {
        _sessionState.value = SessionState.Refreshing
        return try {
            val refreshed = tokenRefresher.refresh(tokens, PkceGeneratorState.next())
            tokenStorage.saveTokens(refreshed)
            updateAuthenticated(refreshed.userId, refreshed.expiresAtEpochSeconds)
            refreshed.accessToken
        } catch (_: TokenRefreshException) {
            tokenStorage.clear()
            _sessionState.value = SessionState.Unauthenticated
            _zone.value = AuthZone.Unauthorized
            throw IllegalStateException("Session expired")
        }
    }

    private fun updateAuthenticated(userId: Long, expiresAtEpochSeconds: Long?) {
        _sessionState.value = SessionState.Authenticated(userId, expiresAtEpochSeconds)
        _zone.value = AuthZone.Authorized
    }

    private fun VkTokens.isValid(): Boolean {
        val expiresAt = expiresAtEpochSeconds ?: return true
        val now = Clock.System.now().epochSeconds
        return expiresAt > now + REFRESH_THRESHOLD_SECONDS
    }

    private companion object {
        const val REFRESH_THRESHOLD_SECONDS = 60L
    }
}

private object PkceGeneratorState {
    fun next(): String = buildString {
        repeat(32) { append(ALLOWED.random()) }
    }

    private val ALLOWED = (('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('_', '-')).joinToString("")
}
