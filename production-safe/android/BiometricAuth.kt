package com.umartech.umarae.security

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private val supportsCombinedAuthenticators = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

/**
 * Gates a sensitive action (revealing the wallet recovery phrase) behind the
 * device's own fingerprint/face/PIN. If the device has no lock screen
 * configured at all, there's nothing to authenticate against, so this falls
 * through to [onSuccess] rather than blocking the user entirely.
 */
fun FragmentActivity.requestDeviceAuth(
    title: String,
    subtitle: String,
    onSuccess: () -> Unit,
    onFailure: (() -> Unit)? = null,
) {
    val manager = BiometricManager.from(this)
    val checkAuthenticators = if (supportsCombinedAuthenticators) BIOMETRIC_WEAK or DEVICE_CREDENTIAL else BIOMETRIC_WEAK
    when (manager.canAuthenticate(checkAuthenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            val prompt = BiometricPrompt(
                this,
                ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        onFailure?.invoke()
                    }
                },
            )
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .apply {
                    if (supportsCombinedAuthenticators) {
                        setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
                    } else {
                        @Suppress("DEPRECATION")
                        setDeviceCredentialAllowed(true)
                    }
                }
                .build()
            prompt.authenticate(promptInfo)
        }
        else -> onSuccess()
    }
}
