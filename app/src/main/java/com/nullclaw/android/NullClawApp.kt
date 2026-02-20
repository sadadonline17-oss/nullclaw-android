package com.nullclaw.android

import android.app.Application
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Application class for NullClaw Android client.
 * Manages global application state and encrypted preferences.
 */
class NullClawApp : Application() {

    companion object {
        lateinit var instance: NullClawApp
            private set

        fun getEncryptedPreferences(context: Context) = EncryptedSharedPreferences.create(
            context,
            "nullclaw_secure_prefs",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}