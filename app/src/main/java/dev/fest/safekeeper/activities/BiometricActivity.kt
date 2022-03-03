package dev.fest.safekeeper.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import dev.fest.safekeeper.R
import dev.fest.safekeeper.databinding.ActivityBiometricBinding
import dev.fest.safekeeper.utilities.BiometricAuthListener
import dev.fest.safekeeper.utilities.BiometricUtils
import dev.fest.safekeeper.utilities.BiometricUtils.hasBiometricCapability
import dev.fest.safekeeper.utilities.BiometricUtils.isBiometricReady


class BiometricActivity : AppCompatActivity(), BiometricAuthListener {
    private lateinit var binding: ActivityBiometricBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAnimationBackground()
    }

    override fun onStart() {
        super.onStart()
        binding.buttonBiometricsLogin.setOnClickListener {
            initBiometric()
        }
        initBiometric()
        if (hasBiometricCapability(this) == 0) {
            if (isBiometricReady(this)) {
                initBiometric()
            }
        }
    }

    private fun initBiometric() {
        BiometricUtils.showBiometricPrompt(
            activity = this,
            listener = this,
            cryptoObject = null,
            allowDeviceCredential = true
        )
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        navigateToListActivity()
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            .show()
        Toast.makeText(this, getString(R.string.biometric_activity_notification), Toast.LENGTH_LONG).show()
    }

    private fun navigateToListActivity() {
        startActivity(Intent(this, PasswordItemActivity::class.java))
        finish()
    }

    private fun initAnimationBackground() {
        val animationDrawable = binding.biometricLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(4000)
        animationDrawable.start()
    }
}