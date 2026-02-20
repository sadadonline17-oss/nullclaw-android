package com.nullclaw.android.data.api

import com.nullclaw.android.data.model.ChatRequest
import com.nullclaw.android.data.model.ChatResponse
import com.nullclaw.android.data.model.HealthResponse
import com.nullclaw.android.data.model.PairResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API interface for NullClaw backend.
 * 
 * Endpoints:
 * - GET /health - Health check (no auth required)
 * - POST /pair - Exchange pairing code for bearer token
 * - POST /webhook - Send chat message (requires bearer token)
 */
interface NullClawApi {

    /**
     * Health check endpoint - always public.
     */
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    /**
     * Pair with the gateway using a one-time pairing code.
     * Returns a bearer token on success.
     * 
     * @param pairingCode The 6-digit pairing code displayed by the gateway
     */
    @POST("pair")
    suspend fun pair(
        @Header("X-Pairing-Code") pairingCode: String
    ): Response<PairResponse>

    /**
     * Send a chat message to the NullClaw agent.
     * 
     * @param authorization Bearer token from pairing
     * @param request Chat request with message and optional session_id
     */
    @POST("webhook")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}