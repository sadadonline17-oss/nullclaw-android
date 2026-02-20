package com.nullclaw.android.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model from NullClaw webhook endpoint.
 */
data class ChatResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("response")
    val response: String? = null,
    @SerializedName("error")
    val error: String? = null
) {
    val isSuccess: Boolean
        get() = status == "ok"

    val message: String
        get() = response ?: error ?: "Unknown response"
}

/**
 * Response model from NullClaw pairing endpoint.
 */
data class PairResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("error")
    val error: String? = null
) {
    val isSuccess: Boolean
        get() = status == "paired"
}

/**
 * Response model from NullClaw health endpoint.
 */
data class HealthResponse(
    @SerializedName("status")
    val status: String
) {
    val isHealthy: Boolean
        get() = status == "ok"
}