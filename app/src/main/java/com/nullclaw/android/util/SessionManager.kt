package com.nullclaw.android.util

import android.content.Context
import com.nullclaw.android.NullClawApp
import java.util.UUID

/**
 * Manages user session state including:
 * - Bearer token (stored encrypted)
 * - Server URL
 * - Unique device/session ID
 */
class SessionManager(private val context: Context) {

    private val encryptedPrefs = NullClawApp.getEncryptedPreferences(context)

    companion object {
        private const val KEY_BEARER_TOKEN = "bearer_token"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_PAIRING_CODE = "pairing_code"

        const val DEFAULT_SERVER_URL = "http://10.0.2.2:3000" // Android emulator localhost
    }

    /**
     * Get the unique device/session ID.
     * Generated once and persisted for conversation continuity.
     */
    fun getSessionId(): String {
        var sessionId = encryptedPrefs.getString(KEY_SESSION_ID, null)
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString()
            encryptedPrefs.edit().putString(KEY_SESSION_ID, sessionId).apply()
        }
        return sessionId
    }

    /**
     * Save the bearer token after successful pairing.
     */
    fun saveBearerToken(token: String) {
        encryptedPrefs.edit().putString(KEY_BEARER_TOKEN, token).apply()
    }

    /**
     * Get the stored bearer token.
     */
    fun getBearerToken(): String? {
        return encryptedPrefs.getString(KEY_BEARER_TOKEN, null)
    }

    /**
     * Clear the bearer token (logout).
     */
    fun clearBearerToken() {
        encryptedPrefs.edit().remove(KEY_BEARER_TOKEN).apply()
    }

    /**
     * Save the server URL.
     */
    fun saveServerUrl(url: String) {
        encryptedPrefs.edit().putString(KEY_SERVER_URL, url).apply()
    }

    /**
     * Get the stored server URL.
     */
    fun getServerUrl(): String {
        return encryptedPrefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }

    /**
     * Check if user is authenticated (has valid bearer token).
     */
    fun isAuthenticated(): Boolean {
        return getBearerToken() != null
    }

    /**
     * Clear all session data (full logout).
     */
    fun clearSession() {
        encryptedPrefs.edit()
            .remove(KEY_BEARER_TOKEN)
            .remove(KEY_PAIRING_CODE)
            .apply()
    }

    /**
     * Save pairing code temporarily.
     */
    fun savePairingCode(code: String) {
        encryptedPrefs.edit().putString(KEY_PAIRING_CODE, code).apply()
    }

    /**
     * Get stored pairing code.
     */
    fun getPairingCode(): String? {
        return encryptedPrefs.getString(KEY_PAIRING_CODE, null)
    }
}