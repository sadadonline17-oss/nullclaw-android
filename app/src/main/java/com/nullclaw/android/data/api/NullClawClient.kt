package com.nullclaw.android.data.api

import com.nullclaw.android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * NullClaw API client configuration.
 * Provides a singleton Retrofit instance with OkHttp client.
 */
object NullClawClient {

    private const val TIMEOUT_SECONDS = 60L

    private var baseUrl: String = BuildConfig.NULLCLAW_BASE_URL
    private var bearerToken: String? = null

    /**
     * Update the base URL for the NullClaw backend.
     */
    fun setBaseUrl(url: String) {
        baseUrl = url.removeSuffix("/")
    }

    /**
     * Set the bearer token for authenticated requests.
     */
    fun setBearerToken(token: String?) {
        bearerToken = token
    }

    /**
     * Get the current bearer token.
     */
    fun getBearerToken(): String? = bearerToken

    /**
     * Get the current base URL.
     */
    fun getBaseUrl(): String = baseUrl

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder()
        val token = bearerToken
        if (token != null) {
            request.addHeader("Authorization", "${NullClawApi.BEARER_PREFIX}$token")
        }
        chain.proceed(request.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NullClawApi by lazy {
        retrofit.create(NullClawApi::class.java)
    }
}