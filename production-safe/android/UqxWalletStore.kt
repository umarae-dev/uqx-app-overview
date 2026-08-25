package com.umartech.umarae.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.umartech.umarae.crypto.UqxWalletCrypto

/** Encrypted, on-device-only storage for the user's non-custodial UQX wallet.
 * Same Android Keystore-backed pattern as [TokenStore] — nothing here is
 * ever sent to a Zynost server. */
class UqxWalletStore(context: Context) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "uqx_wallet_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    val address: String? get() = prefs.getString(KEY_ADDRESS, null)
    val mnemonic: String? get() = prefs.getString(KEY_MNEMONIC, null)
    val privateKeyHex: String? get() = prefs.getString(KEY_PRIVATE_KEY, null)
    val hasWallet: Boolean get() = address != null

    var lastUnlockAt: Long
        get() = prefs.getLong(KEY_LAST_UNLOCK_AT, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_UNLOCK_AT, value).apply()

    fun save(wallet: UqxWalletCrypto.GeneratedWallet) {
        prefs.edit()
            .putString(KEY_ADDRESS, wallet.address)
            .putString(KEY_MNEMONIC, wallet.mnemonic)
            .putString(KEY_PRIVATE_KEY, wallet.privateKeyHex)
            .putLong(KEY_LAST_UNLOCK_AT, System.currentTimeMillis())
            .apply()
    }

    companion object {
        private const val KEY_ADDRESS = "wallet_address"
        private const val KEY_MNEMONIC = "wallet_mnemonic"
        private const val KEY_PRIVATE_KEY = "wallet_private_key"
        private const val KEY_LAST_UNLOCK_AT = "wallet_last_unlock_at"
        const val INACTIVITY_THRESHOLD_MS = 7L * 24 * 60 * 60 * 1000
    }
}
