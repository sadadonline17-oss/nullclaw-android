package com.nullclaw.android.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for sending a message to NullClaw webhook endpoint.
 */
data class ChatRequest(
    @SerializedName("message")
    val message: String,
    @SerializedName("session_id")
    val sessionId: String? = null
)

/**
 * Request model for pairing with NullClaw gateway.
 */
data class PairRequest(
    @SerializedName("pairing_code")
    val pairingCode: String
)