package com.umartech.umarae.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Android Keystore-backed auth-token storage.
 *
 * EncryptedSharedPreferences can become unreadable after a device/cloud restore
 * because the encrypted SharedPreferences file may be restored while its
 * original Android Keystore key is not. Auth state is disposable, so if that
 * happens we delete ONLY the auth preferences and recreate them. Wallet storage
 * is deliberately untouched.
 */
class TokenStore(context: Context) {
    private val appContext = context.applicationContext

    private val prefs: SharedPreferences? by lazy {
        createEncryptedPrefsOrRecover()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            appContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun createEncryptedPrefsOrRecover(): SharedPreferences? {
        return try {
            createEncryptedPrefs()
        } catch (_: Exception) {
            runCatching { appContext.deleteSharedPreferences(PREFS_NAME) }
            runCatching { createEncryptedPrefs() }.getOrNull()
        }
    }

    var token: String?
        get() {
            val encrypted = runCatching { prefs?.getString(KEY_TOKEN, null) }.getOrNull()
            return encrypted ?: processFallbackToken
        }
        set(value) {
            processFallbackToken = value
            runCatching {
                prefs?.edit()?.apply {
                    if (value == null) remove(KEY_TOKEN) else putString(KEY_TOKEN, value)
                }?.apply()
            }
        }

    fun clear() {
        token = null
    }

    companion object {
        private const val PREFS_NAME = "umarae_auth_prefs"
        private const val KEY_TOKEN = "auth_token"

        @Volatile
        private var processFallbackToken: String? = null
    }
}
