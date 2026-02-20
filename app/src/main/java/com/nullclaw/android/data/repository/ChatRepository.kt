package com.nullclaw.android.data.repository

import com.nullclaw.android.data.api.NullClawApi
import com.nullclaw.android.data.api.NullClawClient
import com.nullclaw.android.data.model.ChatRequest
import com.nullclaw.android.data.model.ChatResponse
import com.nullclaw.android.data.model.HealthResponse
import com.nullclaw.android.data.model.PairResponse
import com.nullclaw.android.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for chat operations with NullClaw backend.
 * Handles API calls and session management.
 */
class ChatRepository(private val sessionManager: SessionManager) {

    private val api: NullClawApi
        get() = NullClawClient.api

    /**
     * Check if the NullClaw backend is healthy.
     */
    suspend fun checkHealth(): Result<HealthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.checkHealth()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Health check failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Pair with the NullClaw gateway using a pairing code.
     * On success, stores the bearer token in session.
     */
    suspend fun pair(pairingCode: String): Result<PairResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.pair(pairingCode)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.isSuccess && body.token != null) {
                    sessionManager.saveBearerToken(body.token)
                    NullClawClient.setBearerToken(body.token)
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: "Pairing failed"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Pairing failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a chat message to the NullClaw agent.
     */
    suspend fun sendMessage(message: String): Result<ChatResponse> = withContext(Dispatchers.IO) {
        try {
            val token = sessionManager.getBearerToken()
                ?: return@withContext Result.failure(Exception("Not authenticated"))

            val sessionId = sessionManager.getSessionId()
            val request = ChatRequest(
                message = message,
                sessionId = sessionId
            )

            val authorization = "${NullClawApi.BEARER_PREFIX}$token"
            val response = api.sendMessage(authorization, request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.isSuccess) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.error ?: "Unknown error"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Request failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if user is authenticated.
     */
    fun isAuthenticated(): Boolean {
        return sessionManager.isAuthenticated()
    }

    /**
     * Clear session (logout).
     */
    fun logout() {
        sessionManager.clearSession()
        NullClawClient.setBearerToken(null)
    }

    /**
     * Initialize client with stored session.
     */
    fun initializeSession() {
        val token = sessionManager.getBearerToken()
        val serverUrl = sessionManager.getServerUrl()
        
        NullClawClient.setBaseUrl(serverUrl)
        NullClawClient.setBearerToken(token)
    }

    /**
     * Update server URL.
     */
    fun updateServerUrl(url: String) {
        sessionManager.saveServerUrl(url)
        NullClawClient.setBaseUrl(url)
    }
}