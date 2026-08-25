package com.umartech.umarae.crypto

import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom

/**
 * Real, non-custodial BIP39 + secp256k1 wallet generation for an EVM address
 * (BNB Smart Chain). Everything here runs on-device — the mnemonic and
 * derived private key never touch the network or a Zynost server. See
 * [com.umartech.umarae.data.UqxWalletStore] for how the result is persisted
 * (Android Keystore-backed encrypted storage, same pattern as the auth token).
 */
object UqxWalletCrypto {

    data class GeneratedWallet(
        val mnemonic: String,
        val address: String,
        val privateKeyHex: String,
    )

    // Standard Ethereum/EVM derivation path: m/44'/60'/0'/0/0
    private val DERIVATION_PATH = intArrayOf(
        44 or Bip32ECKeyPair.HARDENED_BIT,
        60 or Bip32ECKeyPair.HARDENED_BIT,
        0 or Bip32ECKeyPair.HARDENED_BIT,
        0,
        0,
    )

    fun generate(): GeneratedWallet {
        val entropy = ByteArray(16) // 128-bit entropy -> 12-word mnemonic
        SecureRandom().nextBytes(entropy)
        return fromMnemonic(MnemonicUtils.generateMnemonic(entropy))
    }

    fun fromMnemonic(mnemonic: String): GeneratedWallet {
        val seed = MnemonicUtils.generateSeed(mnemonic, null)
        val master = Bip32ECKeyPair.generateKeyPair(seed)
        val derived = Bip32ECKeyPair.deriveKeyPair(master, DERIVATION_PATH)
        val credentials = Credentials.create(derived)
        return GeneratedWallet(
            mnemonic = mnemonic,
            address = credentials.address,
            privateKeyHex = derived.privateKey.toString(16),
        )
    }

    fun isValidMnemonic(mnemonic: String): Boolean = MnemonicUtils.validateMnemonic(mnemonic.trim())
}
